import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * 
 * ShoppingList
 * A string-based shopping list component with toggleable purchased items and a title header. 
 */
public class ShoppingList extends JList<String> {
    private final DefaultListModel<String> itemModel;
    private final List<Boolean> purchasedItems = new ArrayList<>();
    private final JLabel headerLabel;
    
    //filter variables that search all items, index and create a new field for array.
    private final List<String> allItems = new ArrayList<>(); 
    private final List<Integer> indexes = new ArrayList<>();
    private final JTextField filterField = new JTextField(20);

    /**
     * update the item based on the filter text
     * item that doesn't match are hidden
     */
    private void refilter() { 
        String term = filterField.getText();

        itemModel.clear();
        indexes.clear();

        for (int index = 0; index < allItems.size(); index++) {
            if (allItems.get(index).indexOf(term, 0) != -1) {
                itemModel.addElement(allItems.get(index));
                indexes.add(index);
            }
        }
    }

    /**
    *
    * Creates a shopping list with the given title and initial items
    *  
    * @param title text shown in the list's column header.
    * @param initialItems items to displau initially; may be blank 
    * 
    * new ShoppingList("Title", "Milk", "Eggs", "Cheese")
    */
    public ShoppingList(String title, String... initialItems) {
        super(new DefaultListModel<>());
        this.itemModel = (DefaultListModel<String>) getModel();
        this.headerLabel = new JLabel(title);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellRenderer(new ShoppingListItemRenderer());
        for (String item : initialItems) {
            addItem(item);
        }
        addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                    toggleItemAt(mouseEvent.getPoint());
                }
            }
        });
        getActionMap().put("togglePurchased", new AbstractAction() {
           @Override
           public void actionPerformed(ActionEvent event) {
              // toggle selected item as purchased
              toggleSelectedItem();
           } 
        });
        //document listener from swing hack #13
        filterField.getDocument().addDocumentListener(
            new DocumentListener() {

                @Override 
                public void insertUpdate(DocumentEvent e) {
                    refilter();
                }

                @Override 
                public void removeUpdate(DocumentEvent e) {
                    refilter();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refilter();
                }
        });
    }

    /**
     * Creates a scroll pane with this list and its title header.
     * 
     * @return JScrollPane scrollPane
     */
    public JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this);
        JPanel header = new JPanel(new BorderLayout());
        header.add(headerLabel, BorderLayout.NORTH);
        header.add(filterField, BorderLayout.CENTER);
        scrollPane.setColumnHeaderView(header);
        return scrollPane;
    }

    /**
     * add an item to the shopping list
     * item is stored in allItem since it won't be lost when the list got filter
     * @param item name of the item
     */
    public void addItem(String item) {
        allItems.add(item);
        purchasedItems.add(false);
        refilter();
    }

    /**
     * clear every item and it's purchased status
     */
    public void clearItems() {
        allItems.clear();
        purchasedItems.clear();
        refilter();
    }

    /**
     * remove the selected item from the list
     * get originalIndex of an item because when it is filtered it might change positions
     * update the display list by calling refilter
     */
    public void removeSelectedItem() {
        int index = getSelectedIndex();
        if (index >= 0) {
            int originalIndex = indexes.get(index);
            
            allItems.remove(originalIndex);
            purchasedItems.remove(originalIndex);
            refilter();
        }
    }

    /**
     * remove all items that have been marked as purchase
     * loop backwards to prevent index from shifting when removing item
     */
    public void removePurchasedItems() {
        for (int index = purchasedItems.size() - 1; index >= 0; index--) {
            if (purchasedItems.get(index)) {
                allItems.remove(index);
                purchasedItems.remove(index);
            }
        }
        refilter();
    }

    /**
     * Returns the current item text in display order.
     * 
     * @return List<String> items
     */
    public List<String> getItems() {
        return new ArrayList<>(allItems);
    } 

    /**
     * toggles the selected item to mark if have been purchase or not
     * get the index of the item and call teh togglepurchase
     */
    private void toggleSelectedItem() {
        int index = getSelectedIndex();
        if (index >= 0) {
            togglePurchased(index);
        }
    }

    /**
     * toggle the purchase status of an item
     * uses original index since filtering change positions
     * @param index
     */
    private void togglePurchased(int index) {
        int originalIndex = indexes.get(index);

        purchasedItems.set(originalIndex, !purchasedItems.get(originalIndex));
        repaint(getCellBounds(index, index));
    }

    /**
     * toggle the purchase status of an item click by the user
     * finds the item at the position of the cursor
     * @param point position of mouse
     */
    private void toggleItemAt(Point point) {
        int index = locationToIndex(point);
        if (index >= 0 && getCellBounds(index, index).contains(point)) {
            setSelectedIndex(index);
            togglePurchased(index);
        }
    }

    /**
    * ShoppingListItemRenderer overrides the default rendering behavior of list items so that
    * the app can modify text attributes of purchased rows to reflect the purchasedItems list.
    * We must use a HashMap to modify the font of a component in a ListCellRenderer.
    * 
    */
    private final class ShoppingListItemRenderer extends DefaultListCellRenderer {
        @Override 
        public Component getListCellRendererComponent(JList<?> list,
                                              Object value,
                                              int index,
                                              boolean isSelected,
                                              boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setText(value == null ? "" : value.toString());
            if (index >= 0 && index < purchasedItems.size() && purchasedItems.get(index)) {
                Map<TextAttribute, Object> attributes = new HashMap<>();
                attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                Font font = getFont();
                setFont(font.deriveFont(attributes));
            }
            return this;                                 
        }
    }
}