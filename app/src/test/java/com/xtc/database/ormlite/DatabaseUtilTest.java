package com.xtc.database.ormlite;

import com.j256.ormlite.table.DatabaseTable;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DatabaseUtilTest {

    @Test
    public void tableNameUsesDatabaseTableAnnotation() {
        assertEquals("custom_moment", DatabaseUtil.getTableName(AnnotatedEntity.class));
    }

    @Test
    public void columnNamesPreserveOriginalOrder() {
        List<ColumnStruct> columns = Arrays.asList(
                new ColumnStruct("id", "INTEGER"),
                new ColumnStruct("content", "VARCHAR"),
                new ColumnStruct("createTime", "BIGINT"));

        assertEquals(Arrays.asList("id", "content", "createTime"),
                DatabaseUtil.getColumnNames(columns));
        assertTrue(DatabaseUtil.getColumnNames(null).isEmpty());
    }

    @Test
    public void parseCreateTableSqlExtractsColumns() {
        String sql = "CREATE TABLE `moment` (`id` INTEGER, `content` VARCHAR, `createTime` BIGINT)";

        List<ColumnStruct> columns = DatabaseUtil.parseCreateTableSql(sql);

        assertEquals(3, columns.size());
        assertEquals("id", columns.get(0).getColumnName());
        assertEquals("INTEGER", columns.get(0).getColumnLimit());
        assertEquals("content", columns.get(1).getColumnName());
        assertEquals("VARCHAR", columns.get(1).getColumnLimit());
        assertEquals("createTime", columns.get(2).getColumnName());
        assertEquals("BIGINT", columns.get(2).getColumnLimit());
    }

    @Test
    public void addedColumnsUseNewSchemaOrder() {
        assertEquals(Arrays.asList("d", "c"),
                DatabaseUtil.getAddedColumns(Arrays.asList("a", "b"),
                        Arrays.asList("b", "d", "a", "c")));
        assertTrue(DatabaseUtil.getAddedColumns(null, Arrays.asList("a")).isEmpty());
        assertTrue(DatabaseUtil.getAddedColumns(Arrays.asList("a"), null).isEmpty());
    }

    @Test
    public void findColumnMatchesExactName() {
        ColumnStruct content = new ColumnStruct("content", "VARCHAR");
        List<ColumnStruct> columns = Arrays.asList(new ColumnStruct("id", "INTEGER"), content);

        assertEquals(content, DatabaseUtil.findColumn("content", columns));
        assertNull(DatabaseUtil.findColumn("Content", columns));
        assertNull(DatabaseUtil.findColumn(null, columns));
        assertNull(DatabaseUtil.findColumn("id", null));
    }

    @Test
    public void columnChangesDetectLimitDifferences() {
        List<ColumnStruct> oldColumns = Arrays.asList(
                new ColumnStruct("id", "INTEGER"),
                new ColumnStruct("content", "VARCHAR"));
        List<ColumnStruct> sameColumns = Arrays.asList(
                new ColumnStruct("id", "INTEGER"),
                new ColumnStruct("content", "VARCHAR"));
        List<ColumnStruct> changedColumns = Arrays.asList(
                new ColumnStruct("id", "BIGINT"),
                new ColumnStruct("content", "VARCHAR"));

        assertFalse(DatabaseUtil.hasColumnChanged(oldColumns, sameColumns));
        assertTrue(DatabaseUtil.hasColumnChanged(oldColumns, changedColumns));
        assertFalse(DatabaseUtil.hasColumnChanged(null, changedColumns));
        assertFalse(DatabaseUtil.hasColumnChanged(oldColumns, null));
    }

    @Test
    public void missingOldColumnsCanBeSelectedForCopy() {
        assertEquals("`legacy`",
                DatabaseUtil.buildSharedColumns(
                        Arrays.asList("id", "legacy"), Arrays.asList("id", "content")));
        assertTrue(DatabaseUtil.buildSharedColumns(null,
                Collections.singletonList("id")).isEmpty());
    }

    @DatabaseTable(tableName = "custom_moment")
    private static final class AnnotatedEntity {
    }

}
