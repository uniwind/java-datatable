import io.vavr.collection.Vector;

/**
 * DataRowCollection. Handles a collection of DataRows
 * Created by Martin Cooper on 17/07/2017.
 */
public class DataRowCollection {

    private final DataTable table;
    private final Vector<DataRow> rows;
    private final Integer rowCount;

    /**
     * Private DataRow constructor.
     * Use 'build' to create instance.
     * @param table The DataTable the DataRow is pointing to.
     * @param rows The DataRows.
     */
    private DataRowCollection(DataTable table, Iterable<DataRow> rows) {
        this.table = table;
        this.rows = Vector.ofAll(rows);
        this.rowCount = this.rows.length();
    }
}
