import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ProductTableModel extends AbstractTableModel {
    private List<Product> products;
    private final String[] columnNames = { "Name", "Price", "Quantity" };

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (products.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        double discountPercentage = 10; 

        switch (columnIndex) {
            case 0:
                return product.getName();
            case 1:
                return product.getPrice();
            case 2:
                return product.getQuantity();
            default:
                throw new IndexOutOfBoundsException("Invalid column index");
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        switch (columnIndex) {
            case 0:
                product.setName((String) value);
                break;
            case 1:
                product.setPrice((double) value);
                break;
            case 2:
                product.setQuantity((int) value);
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid column index");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void addProduct(Product product) {
        products.add(product);
        int row = products.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public void removeProduct(int rowIndex) {
        products.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}
