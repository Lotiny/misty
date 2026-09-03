package me.lotiny.misty.storage.impl.sql;

import me.lotiny.misty.storage.StorageRegistry;
import me.lotiny.misty.storage.StorageSerializer;
import me.lotiny.misty.storage.impl.AbstractSqlStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PostgresqlStorage<T> extends AbstractSqlStorage<T> {

    public PostgresqlStorage(
            StorageRegistry storageRegistry, String uniqueKey, String tableName, StorageSerializer<T> serializer) {
        super(storageRegistry, uniqueKey, tableName, serializer);
    }

    @Override
    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + uniqueKey + " VARCHAR(255) PRIMARY KEY, "
                + dataColumn + " JSONB"
                + ");";
    }

    @Override
    protected String getFindJsonSql(String key) {
        String pgPath = Arrays.stream(key.split("\\.")).collect(Collectors.joining(",", "{", "}"));
        return "SELECT " + dataColumn + " FROM " + tableName + " WHERE " + dataColumn + " #>> '" + pgPath
                + "' = ? LIMIT 1";
    }

    @Override
    protected String getTopsSql(String key) {
        return "SELECT " + dataColumn + " FROM " + tableName
                + " ORDER BY CAST(" + dataColumn + " #>> '{stats," + key + "}' AS BIGINT) DESC NULLS LAST"
                + " LIMIT ?";
    }

    @Override
    protected String getUpsertSql() {
        return "INSERT INTO " + tableName + " (" + uniqueKey + ", " + dataColumn + ")"
                + " VALUES (?, ?::jsonb)"
                + " ON CONFLICT (" + uniqueKey + ") DO UPDATE SET " + dataColumn + " = EXCLUDED." + dataColumn;
    }

    @Override
    protected void bindJsonParam(PreparedStatement pstmt, int index, String json) throws SQLException {
        pstmt.setObject(index, json, Types.OTHER);
    }
}
