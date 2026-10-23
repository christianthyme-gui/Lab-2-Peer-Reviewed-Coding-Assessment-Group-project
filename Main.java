import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Main class to print the Shopping List Application
 * Main
 */
public class Main {

    /**
     * creating borders and frames.
     * uses swingutilities to create the GUI.
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame jFrame = new JFrame("Shopping List Application");
            ShoppingList slist = new ShoppingList("ShoppingList", 
            "Milk", "Eggs", "Cheese", "Bread", "Coffee"
            );

            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setLayout(new BorderLayout());
            jFrame.add(slist.createScrollPane(), BorderLayout.CENTER);
            jFrame.setJMenuBar(createMenuBar(jFrame, slist));
            jFrame.setSize(400, 300);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
        });
    }

    /**
     * create the menu bar for the Shopping List Application.
     * menu bar allow user to create a new shopping list, add item, remove selected items, remove purchase items, and clear the section.
     * each JMenuItem uses and actionListener to perform the action
     * @param frame
     * @param shoppingList
     * @return
     */
    private static JMenuBar createMenuBar(JFrame frame, ShoppingList shoppingList) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        //clears all item to start a new window
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(event -> shoppingList.clearItems());

        //closes the application window
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(event -> frame.dispatchEvent(new 
            WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));

        //clear the selected item without clearing the whole item
        JMenuItem clearItem = new JMenuItem("Clear selection");
        clearItem.addActionListener(event -> shoppingList.clearSelection());

        //remove the selected item from teh shopping list
        JMenuItem removeItem = new JMenuItem("Remove select item");
        removeItem.addActionListener(event -> shoppingList.removeSelectedItem());
        
        //remove all items that have been mark as purchase
        JMenuItem removePruchase = new JMenuItem("Remove purchased Item");
        removePruchase.addActionListener(event -> shoppingList.removePurchasedItems());
        
        //open an input window and adds item into the list
        JMenuItem addItem = new JMenuItem("Add Item");
        addItem.addActionListener(event -> {
            String item = JOptionPane.showInputDialog(frame, "Enter item name: ");
            if (item != null) {
                shoppingList.addItem(item);
            }
        });
        
        fileMenu.add(newItem);
        fileMenu.add(closeItem);
        editMenu.add(clearItem);
        editMenu.add(addItem);
        editMenu.add(removeItem);
        editMenu.add(removePruchase);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        return menuBar;
    }
}