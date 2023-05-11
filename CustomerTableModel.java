import java.util.List;
import javax.swing.table.AbstractTableModel;

public class CustomerTableModel extends AbstractTableModel {
    private List<Customer> customers;
    private final String[] columnNames = { "Name", "Address", "Balance", "Loyalty Points" };

    public CustomerTableModel(List<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public int getRowCount() {
        return customers.size();
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
        if (customers.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return customer.getName();
            case 1:
                return customer.getAddress();
            case 2:

                return customer.getBalance();
            case 3:
                return customer.getLoyaltyPoints();
            default:
                throw new IndexOutOfBoundsException("Invalid column index");
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                customer.setName((String) value);
                break;
            case 1:
                customer.setBalance((double) value);
                break;
            case 2:
                customer.setAddress((String) value);
                break;
            case 3:
                customer.setLoyaltyPoints((int) value);
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid column index");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
        fireTableDataChanged();
    }

    
}
