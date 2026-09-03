package me.lotiny.misty.storage.impl.sql;

import me.lotiny.misty.storage.StorageRegistry;
import me.lotiny.misty.storage.StorageSerializer;
import me.lotiny.misty.storage.impl.AbstractSqlStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlStorage<T> extends AbstractSqlStorage<T> {

    public MySqlStorage(
            StorageRegistry storageRegistry, String uniqueKey, String tableName, StorageSerializer<T> serializer) {
        super(storageRegistry, uniqueKey, tableName, serializer);
    }

    @Override
    protected String getCreateTableSql() {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + uniqueKey + " VARCHAR(255) PRIMARY KEY, "
                + dataColumn + " JSON"
                + ");";
    }

    @Override
    protected String getFindJsonSql(String key) {
        String jsonPath = "'$." + key + "'";
        return "SELECT " + dataColumn + " FROM " + tableName + " WHERE JSON_UNQUOTE(JSON_EXTRACT(" + dataColumn + ", "
                + jsonPath + ")) = ? LIMIT 1";
    }

    @Override
    protected String getTopsSql(String key) {
        String jsonPath = "'$.stats." + key + "'";
        return "SELECT " + dataColumn + " FROM " + tableName
                + " ORDER BY CAST(JSON_UNQUOTE(JSON_EXTRACT(" + dataColumn + ", " + jsonPath + ")) AS SIGNED) DESC"
                + " LIMIT ?";
    }

    @Override
    protected String getUpsertSql() {
        return "INSERT INTO " + tableName + " (" + uniqueKey + ", " + dataColumn + ")"
                + " VALUES (?, ?)"
                + " ON DUPLICATE KEY UPDATE " + dataColumn + " = VALUES(" + dataColumn + ")";
    }

    @Override
    protected void bindJsonParam(PreparedStatement pstmt, int index, String json) throws SQLException {
        pstmt.setString(index, json);
    }
}
