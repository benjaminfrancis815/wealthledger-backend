package com.benjaminfrancis815.wealthledger.stock.repository;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.benjaminfrancis815.wealthledger.stock.model.StockLtpStaging;

@Repository
public class StockBatchRepositoryImpl implements StockBatchRepository {

	private static final Logger log = LoggerFactory.getLogger(StockBatchRepositoryImpl.class);

	private static final String STOCK_LTPS_STAGING_TRUNCATE_SQL = """
			TRUNCATE TABLE stock_ltps_staging
						""";

	private static final String STOCK_LTPS_STAGING_INSERT_SQL = """
			INSERT INTO stock_ltps_staging (
			    symbol,
			    ltp,
			    ltp_date,
			    created_by,
			    modified_by
			)
			VALUES (
			    :symbol,
			    :ltp,
			    :ltpDate,
			    :createdBy,
			    :modifiedBy
			)
						""";

	private static final String STOCK_LTPS_INSERT_SQL = """
			INSERT INTO stock_ltps (
			    stock_id,
			    ltp,
			    ltp_date,
			    created_by,
			    modified_by
			)
			SELECT
			    s.id,
			    sla.ltp::NUMERIC(20,8),
			    sla.ltp_date::DATE,
			    sla.created_by,
			    sla.modified_by
			FROM
			    stock_ltps_staging sla
			    INNER JOIN stocks s ON
			        s.symbol = sla.symbol
			ON CONFLICT (stock_id, ltp_date) DO NOTHING
						""";

	private static final String STOCK_LTPS_UPDATE_SQL = """
			WITH updatable AS (
			    SELECT
			        sla.id,
			        (
			            CASE
			                WHEN slb.id IS NOT NULL AND sla.is_latest = FALSE THEN TRUE
			                ELSE FALSE
			            END
			        ) is_latest
			    FROM
			        stock_ltps sla
			        INNER JOIN stocks s ON
			            s.id = sla.stock_id
			        LEFT JOIN stock_ltps_staging slb ON
			            slb.symbol = s.symbol
			            AND slb.ltp_date::DATE = sla.ltp_date
			    WHERE
			        (slb.id IS NOT NULL AND sla.is_latest = FALSE)
			        OR
			        (slb.id IS NULL AND sla.is_latest = TRUE)
			)
			UPDATE stock_ltps sla
			SET
			    is_latest = u.is_latest,
			    modified_at = NOW()
			FROM
			    updatable u
			WHERE
			    sla.id = u.id
						""";

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final JdbcTemplate jdbcTemplate;

	public StockBatchRepositoryImpl(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.jdbcTemplate = this.namedParameterJdbcTemplate.getJdbcTemplate();
	}

	@Override
	public void cleanupStockLtpsStaging() {
		log.info("Executing query [{}].", STOCK_LTPS_STAGING_TRUNCATE_SQL);
		this.jdbcTemplate.execute(STOCK_LTPS_STAGING_TRUNCATE_SQL);
	}

	@Override
	public int[] saveStockLtpsStaging(final List<StockLtpStaging> stockLtpsStaging) {
		final SqlParameterSource[] sqlParameterSources = stockLtpsStaging.stream().map(x -> {
			return new MapSqlParameterSource().addValue("symbol", x.getSymbol(), Types.VARCHAR)
					.addValue("ltp", x.getLtp(), Types.VARCHAR).addValue("ltpDate", x.getLtpDate(), Types.VARCHAR)
					.addValue("createdBy", x.getCreatedBy(), Types.BIGINT)
					.addValue("modifiedBy", x.getModifiedBy(), Types.BIGINT);
		}).toArray(SqlParameterSource[]::new);
		log.info("Executing query [{}] with [{}] records.", STOCK_LTPS_STAGING_INSERT_SQL, sqlParameterSources.length);
		final int[] insertedRows = this.namedParameterJdbcTemplate.batchUpdate(STOCK_LTPS_STAGING_INSERT_SQL,
				sqlParameterSources);
		log.info("Inserted rows [{}].", Arrays.toString(insertedRows));
		return insertedRows;
	}

	@Override
	public int saveStockLtps() {
		log.info("Executing query [{}].", STOCK_LTPS_INSERT_SQL);
		final int insertedRows = this.jdbcTemplate.update(STOCK_LTPS_INSERT_SQL);
		log.info("Inserted rows [{}].", insertedRows);
		return insertedRows;
	}

	@Override
	public int updateStockLtps() {
		log.info("Executing query [{}].", STOCK_LTPS_UPDATE_SQL);
		final int updatedRows = this.jdbcTemplate.update(STOCK_LTPS_UPDATE_SQL);
		log.info("Updated rows [{}].", updatedRows);
		return updatedRows;
	}

}
