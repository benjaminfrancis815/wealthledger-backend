package com.benjaminfrancis815.wealthledger.index.repository;

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

import com.benjaminfrancis815.wealthledger.index.model.IndexValueStaging;

@Repository
public class IndexBatchRepositoryImpl implements IndexBatchRepository {

	private static final Logger log = LoggerFactory.getLogger(IndexBatchRepositoryImpl.class);

	private static final String INDEX_VALUES_STAGING_TRUNCATE_SQL = """
			TRUNCATE TABLE index_values_staging
						""";

	private static final String INDEX_VALUES_STAGING_INSERT_SQL = """
			INSERT INTO index_values_staging (
			    name,
			    high_index_value,
			    low_index_value,
			    closing_index_value,
			    index_date,
			    created_by,
			    modified_by
			)
			VALUES (
			    :name,
			    :highIndexValue,
			    :lowIndexValue,
			    :closingIndexValue,
			    :indexDate,
			    :createdBy,
			    :modifiedBy
			)
						""";

	private static final String INDEX_VALUES_INSERT_SQL = """
			INSERT INTO index_values (
			    index_id,
			    high_index_value,
			    low_index_value,
			    closing_index_value,
			    index_date,
			    created_by,
			    modified_by
			)
			SELECT
			    i.id,
			    iva.high_index_value::NUMERIC(20,8),
			    iva.low_index_value::NUMERIC(20,8),
			    iva.closing_index_value::NUMERIC(20,8),
			    TO_DATE(iva.index_date, 'DD-MM-YYYY') index_date,
			    iva.created_by,
			    iva.modified_by
			FROM
			    index_values_staging iva
			    INNER JOIN indices i ON
			        i.name = iva.name
			ON CONFLICT (index_id, index_date) DO NOTHING
						""";

	private static final String INDEX_VALUES_UPDATE_SQL = """
			WITH updatable AS (
			    SELECT
			        iva.id,
			        (
			            CASE
			                WHEN ivb.id IS NOT NULL AND iva.is_latest = FALSE THEN TRUE
			                ELSE FALSE
			            END
			        ) is_latest
			    FROM
			        index_values iva
			        INNER JOIN indices i ON
			            i.id = iva.index_id
			        LEFT JOIN index_values_staging ivb ON
			            ivb.name = i.name
			            AND TO_DATE(ivb.index_date, 'DD-MM-YYYY') = iva.index_date
			    WHERE
			        (ivb.id IS NOT NULL AND iva.is_latest = FALSE)
			        OR
			        (ivb.id IS NULL AND iva.is_latest = TRUE)
			)
			UPDATE index_values iva
			SET
			    is_latest = u.is_latest,
			    modified_at = NOW()
			FROM
			    updatable u
			WHERE
			    iva.id = u.id
						""";

	private static final String INDEX_METRIC_50_DAY_MOVING_AVERAGE_VALUES_UPDATE_SQL = """
			WITH row_numbered AS (
			    SELECT
			        iv.index_id,
			        iv.closing_index_value,
			        ROW_NUMBER() OVER(PARTITION BY iv.index_id ORDER BY iv.modified_at DESC) AS row_number
			    FROM
			        index_values iv
			),
			updatable AS (
			    SELECT
			        rn.index_id,
			        AVG(rn.closing_index_value) dma_50
			    FROM
			        row_numbered rn
			    WHERE
			        row_number <= 50
			    GROUP BY
			        rn.index_id
			)
			UPDATE index_metric_values ima
			SET
			    dma_50 = u.dma_50,
			    modified_at = NOW()
			FROM
			    updatable u
			WHERE
			    ima.index_id = u.index_id
			""";

	private static final String INDEX_METRIC_200_DAY_MOVING_AVERAGE_VALUES_UPDATE_SQL = """
			WITH row_numbered AS (
			    SELECT
			        iv.index_id,
			        iv.closing_index_value,
			        ROW_NUMBER() OVER(PARTITION BY iv.index_id ORDER BY iv.modified_at DESC) AS row_number
			    FROM
			        index_values iv
			),
			updatable AS (
			    SELECT
			        rn.index_id,
			        AVG(rn.closing_index_value) dma_200
			    FROM
			        row_numbered rn
			    WHERE
			        row_number <= 200
			    GROUP BY
			        rn.index_id
			)
			UPDATE index_metric_values ima
			SET
			    dma_200 = u.dma_200,
			    modified_at = NOW()
			FROM
			    updatable u
			WHERE
			    ima.index_id = u.index_id
			""";

	private static final String INDEX_METRIC_ALL_TIME_HIGH_VALUES_UPDATE_SQL = """
			UPDATE index_metric_values ima SET
			    ath = iv.high_index_value,
			    modified_at = NOW()
			FROM
			    index_values iv
			WHERE
			    ima.index_id = iv.index_id
			    AND iv.is_latest = true
			    AND iv.high_index_value > ima.ath
						""";

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final JdbcTemplate jdbcTemplate;

	public IndexBatchRepositoryImpl(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.jdbcTemplate = this.namedParameterJdbcTemplate.getJdbcTemplate();
	}

	@Override
	public void cleanupIndexValuesStaging() {
		log.info("Executing query [{}].", INDEX_VALUES_STAGING_TRUNCATE_SQL);
		this.jdbcTemplate.execute(INDEX_VALUES_STAGING_TRUNCATE_SQL);
	}

	@Override
	public int[] saveIndexValuesStaging(final List<IndexValueStaging> indexValuesStaging) {
		final SqlParameterSource[] sqlParameterSources = indexValuesStaging.stream().map(x -> {
			return new MapSqlParameterSource().addValue("name", x.getName(), Types.VARCHAR)
					.addValue("highIndexValue", x.getHighIndexValue(), Types.VARCHAR)
					.addValue("lowIndexValue", x.getLowIndexValue(), Types.VARCHAR)
					.addValue("closingIndexValue", x.getClosingIndexValue(), Types.VARCHAR)
					.addValue("indexDate", x.getIndexDate(), Types.VARCHAR)
					.addValue("createdBy", x.getCreatedBy(), Types.BIGINT)
					.addValue("modifiedBy", x.getModifiedBy(), Types.BIGINT);
		}).toArray(SqlParameterSource[]::new);
		log.info("Executing query [{}] with [{}] records.", INDEX_VALUES_STAGING_INSERT_SQL,
				sqlParameterSources.length);
		final int[] insertedRows = this.namedParameterJdbcTemplate.batchUpdate(INDEX_VALUES_STAGING_INSERT_SQL,
				sqlParameterSources);
		log.info("Inserted rows [{}].", Arrays.toString(insertedRows));
		return insertedRows;
	}

	@Override
	public int saveIndexValues() {
		log.info("Executing query [{}].", INDEX_VALUES_INSERT_SQL);
		final int insertedRows = this.jdbcTemplate.update(INDEX_VALUES_INSERT_SQL);
		log.info("Inserted rows [{}].", insertedRows);
		return insertedRows;
	}

	@Override
	public int updateIndexValues() {
		log.info("Executing query [{}].", INDEX_VALUES_UPDATE_SQL);
		final int updatedRows = this.jdbcTemplate.update(INDEX_VALUES_UPDATE_SQL);
		log.info("Updated rows [{}].", updatedRows);
		return updatedRows;
	}

	@Override
	public int updateIndexMetric50DayMovingAverageValues() {
		log.info("Executing query [{}].", INDEX_METRIC_50_DAY_MOVING_AVERAGE_VALUES_UPDATE_SQL);
		final int updatedRows = this.jdbcTemplate.update(INDEX_METRIC_50_DAY_MOVING_AVERAGE_VALUES_UPDATE_SQL);
		log.info("Updated rows [{}].", updatedRows);
		return updatedRows;
	}

	@Override
	public int updateIndexMetric200DayMovingAverageValues() {
		log.info("Executing query [{}].", INDEX_METRIC_200_DAY_MOVING_AVERAGE_VALUES_UPDATE_SQL);
		final int updatedRows = this.jdbcTemplate.update(INDEX_METRIC_200_DAY_MOVING_AVERAGE_VALUES_UPDATE_SQL);
		log.info("Updated rows [{}].", updatedRows);
		return updatedRows;
	}

	@Override
	public int updateIndexMetricAllTimeHighValues() {
		log.info("Executing query [{}].", INDEX_METRIC_ALL_TIME_HIGH_VALUES_UPDATE_SQL);
		final int updatedRows = this.jdbcTemplate.update(INDEX_METRIC_ALL_TIME_HIGH_VALUES_UPDATE_SQL);
		log.info("Updated rows [{}].", updatedRows);
		return updatedRows;
	}

}
