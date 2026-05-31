package com.benjaminfrancis815.wealthledger.mutualfund.repository;

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

import com.benjaminfrancis815.wealthledger.mutualfund.model.MutualFundNavStaging;

@Repository
public class MutualFundBatchRepositoryImpl implements MutualFundBatchRepository {

	private static final Logger log = LoggerFactory.getLogger(MutualFundBatchRepositoryImpl.class);

	private static final String MUTUAL_FUND_NAVS_STAGING_TRUNCATE_SQL = """
			TRUNCATE TABLE mutual_fund_navs_staging
						""";

	private static final String MUTUAL_FUND_NAVS_STAGING_INSERT_SQL = """
			INSERT INTO mutual_fund_navs_staging (
			    scheme_code,
			    nav,
			    nav_date,
			    created_by,
			    modified_by
			)
			VALUES (
			    :schemeCode,
			    :nav,
			    :navDate,
			    :createdBy,
			    :modifiedBy
			)
						""";

	private static final String MUTUAL_FUND_NAVS_INSERT_SQL = """
			INSERT INTO mutual_fund_navs (
			    mutual_fund_id,
			    nav,
			    nav_date,
			    created_by,
			    modified_by
			)
			SELECT
			    mf.id,
			    mfa.nav::NUMERIC(20,8),
			    mfa.nav_date::DATE,
			    mfa.created_by,
			    mfa.modified_by
			FROM
			    mutual_fund_navs_staging mfa
			    INNER JOIN mutual_funds mf ON
			        mf.scheme_code = mfa.scheme_code::BIGINT
			ON CONFLICT (mutual_fund_id, nav_date) DO NOTHING
						""";

	private static final String MUTUAL_FUND_NAVS_UPDATE_SQL = """
			WITH updatable AS (
			    SELECT
			        mfa.id,
			        (
			            CASE
			                WHEN mfb.id IS NOT NULL AND mfa.is_latest = FALSE THEN TRUE
			                ELSE FALSE
			            END
			        ) is_latest
			    FROM
			        mutual_fund_navs mfa
			        INNER JOIN mutual_funds mf ON
			            mf.id = mfa.mutual_fund_id
			        LEFT JOIN mutual_fund_navs_staging mfb ON
			            mfb.scheme_code::BIGINT = mf.scheme_code
			            AND mfb.nav_date::DATE = mfa.nav_date
			    WHERE
			        (mfb.id IS NOT NULL AND mfa.is_latest = FALSE)
			        OR
			        (mfb.id IS NULL AND mfa.is_latest = TRUE)
			)
			UPDATE mutual_fund_navs mfa
			SET
			    is_latest = u.is_latest,
			    modified_at = NOW()
			FROM
			    updatable u
			WHERE
			    mfa.id = u.id
						""";

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private final JdbcTemplate jdbcTemplate;

	public MutualFundBatchRepositoryImpl(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.jdbcTemplate = this.namedParameterJdbcTemplate.getJdbcTemplate();
	}

	@Override
	public void cleanupMutualFundNavsStaging() {
		log.info("Executing query [{}].", MUTUAL_FUND_NAVS_STAGING_TRUNCATE_SQL);
		this.jdbcTemplate.execute(MUTUAL_FUND_NAVS_STAGING_TRUNCATE_SQL);
	}

	@Override
	public int[] saveMutualFundNavsStaging(final List<MutualFundNavStaging> mutualFundNavsStaging) {
		final SqlParameterSource[] sqlParameterSources = mutualFundNavsStaging.stream().map(x -> {
			return new MapSqlParameterSource().addValue("schemeCode", x.getSchemeCode(), Types.VARCHAR)
					.addValue("nav", x.getNav(), Types.VARCHAR).addValue("navDate", x.getNavDate(), Types.VARCHAR)
					.addValue("createdBy", x.getCreatedBy(), Types.BIGINT)
					.addValue("modifiedBy", x.getModifiedBy(), Types.BIGINT);
		}).toArray(SqlParameterSource[]::new);
		log.info("Executing query [{}] with [{}] records.", MUTUAL_FUND_NAVS_STAGING_INSERT_SQL,
				sqlParameterSources.length);
		final int[] insertedRows = this.namedParameterJdbcTemplate.batchUpdate(MUTUAL_FUND_NAVS_STAGING_INSERT_SQL,
				sqlParameterSources);
		log.info("Inserted rows [{}].", Arrays.toString(insertedRows));
		return insertedRows;
	}

	@Override
	public int saveMutualFundNavs() {
		log.info("Executing query [{}].", MUTUAL_FUND_NAVS_INSERT_SQL);
		final int insertedRows = this.jdbcTemplate.update(MUTUAL_FUND_NAVS_INSERT_SQL);
		log.info("Inserted rows [{}].", insertedRows);
		return insertedRows;
	}

	@Override
	public int updateMutualFundNavs() {
		log.info("Executing query [{}].", MUTUAL_FUND_NAVS_UPDATE_SQL);
		final int updatedRows = this.jdbcTemplate.update(MUTUAL_FUND_NAVS_UPDATE_SQL);
		log.info("Updated rows [{}].", updatedRows);
		return updatedRows;
	}

}
