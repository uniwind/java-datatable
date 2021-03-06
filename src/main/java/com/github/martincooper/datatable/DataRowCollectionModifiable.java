package com.github.martincooper.datatable;

import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;

/**
 * DataRowCollectionModifiable. Handles a collection of DataRows
 * Created by Martin Cooper on 17/07/2017.
 */
public class DataRowCollectionModifiable extends DataRowCollectionBase {

    /**
     * Private DataRow constructor.
     * Use 'build' to create instance.
     *
     * @param table The DataTable the DataRow is pointing to.
     * @param rows The DataRows.
     */
    private DataRowCollectionModifiable(DataTable table, Iterable<DataRow> rows) {
        super(table, rows);
    }

    /**
     * Returns a new DataTable with the additional row appended.
     *
     * @param rowValues The values to append to the row.
     * @return Returns a new DataTable with the row appended.
     */
    public Try<DataTable> addValues(Object ... rowValues) {
        return add(rowValues);
    }

    /**
     * Returns a new DataTable with the additional row appended.
     *
     * @param rowValues The values to append to the row.
     * @return Returns a new DataTable with the row appended.
     */
    public Try<DataTable> add(Object[] rowValues) {
        return Match(mapValuesToColumns(Stream.of(rowValues))).of(
                Case($Success($()), this::addRow),
                Case($Failure($()), Try::failure)
        );
    }

    /**
     * Returns a new DataTable with the additional row inserted at the specified index.
     *
     * @param idx The row index.
     * @param rowValues The values to insert into the row.
     * @return Returns a new DataTable with the row inserted.
     */
    public Try<DataTable> insertValues(int idx, Object ... rowValues) {
        return insert(idx, rowValues);
    }

    /**
     * Returns a new DataTable with the additional row inserted at the specified index.
     *
     * @param idx The row index.
     * @param rowValues The values to insert into the row.
     * @return Returns a new DataTable with the row inserted.
     */
    public Try<DataTable> insert(int idx, Object[] rowValues) {
        return Match(mapValuesToColumns(Stream.of(rowValues))).of(
                Case($Success($()), values -> insertRow(idx, values)),
                Case($Failure($()), Try::failure)
        );
    }

    /**
     * Returns a new DataTable with the data replaced at the specified index.
     *
     * @param idx The row index.
     * @param rowValues The new values to replaced the old ones.
     * @return Returns a new DataTable with the row inserted.
     */
    public Try<DataTable> replaceValues(int idx, Object ... rowValues) {
        return replace(idx, rowValues);
    }

    /**
     * Returns a new DataTable with the data replaced at the specified index.
     *
     * @param idx The row index.
     * @param rowValues The new values to replaced the old ones.
     * @return Returns a new DataTable with the row inserted.
     */
    public Try<DataTable> replace(int idx, Object[] rowValues) {
        return Match(mapValuesToColumns(Stream.of(rowValues))).of(
                Case($Success($()), values -> replaceRow(idx, values)),
                Case($Failure($()), Try::failure)
        );
    }

    /**
     * Returns a new DataTable with the specified row removed.
     *
     * @param idx The row index.
     * @return Returns a new DataTable with the row removed.
     */
    public Try<DataTable> remove(int idx) {
        return removeRow(idx);
    }

    private Try<DataTable> addRow(Seq<ColumnValuePair> values) {
        Try<Seq<IDataColumn>> newCols = toSequence(values.map(val -> val.column().add(val.value())));
        return buildTable(newCols);
    }

    private Try<DataTable> insertRow(int idx, Seq<ColumnValuePair> values) {
        Try<Seq<IDataColumn>> newCols = toSequence(values.map(val -> val.column().insert(idx, val.value())));
        return buildTable(newCols);
    }

    private Try<DataTable> replaceRow(int idx, Seq<ColumnValuePair> values) {
        Try<Seq<IDataColumn>> newCols = toSequence(values.map(val -> val.column().replace(idx, val.value())));
        return buildTable(newCols);
    }

    private Try<DataTable> removeRow(int idx) {
        Try<Seq<IDataColumn>> cols = toSequence(table.columns().map(col -> col.remove(idx)));
        return buildTable(cols);
    }

    private Try<DataTable> buildTable(Try<Seq<IDataColumn>> columns) {
        return Match(columns).of(
                Case($Success($()), cols -> DataTable.build(table.name(), cols)),
                Case($Failure($()), Try::failure)
        );
    }

    /**
     * Validates the number of values equals the number of columns in the table.
     *
     * @param values The values.
     * @return Returns a sequence of ColumnValuePairs if valid.
     */
    private Try<Seq<ColumnValuePair>> mapValuesToColumns(Seq<Object> values) {
        return values.length() != table.columns().count()
                ? DataTableException.tryError("Number of values does not match number of columns.")
                : Try.success(createIndexedColumnValuePair(values));
    }

    /**
     * Maps each value to a corresponding column.
     *
     * @param values The list of values.
     * @return Returns a sequence of column to value mappings.
     */
    private Seq<ColumnValuePair> createIndexedColumnValuePair(Seq<Object> values) {
        return values.zipWithIndex((value, index) -> new ColumnValuePair(table.column(index), value));
    }

    /**
     * Converts a Seq<Try<IDataColumn>> into a Try<Seq<IDataColumn>>
     *
     * @param items The values to convert.
     * @return Returns the converted items.
     */
    private Try<Seq<IDataColumn>> toSequence(Seq<Try<IDataColumn>> items) {
        return Try.sequence(items);
    }

    /**
     * Builds a new DataRowCollection for the specified DataTable.
     *
     * @param table The table to build the DataRowCollection for.
     * @return Returns the DataRowCollection.
     */
    public static DataRowCollectionModifiable build(DataTable table) {
        return buildRowCollection(table, DataRowCollectionModifiable::new);
    }
}
