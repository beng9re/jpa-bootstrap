package jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import persistence.mapper.RowMapper;

public class JdbcTemplate {
    private final Connection connection;

    public JdbcTemplate(final Connection connection) {
        this.connection = connection;
    }

    public void execute(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Long insertForGenerateKey(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql, Statement.RETURN_GENERATED_KEYS);
            final ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            if (resultSet.next()) {
                return rowMapper.mapRow(resultSet);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForAll(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return rowMapper.mapRow(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
