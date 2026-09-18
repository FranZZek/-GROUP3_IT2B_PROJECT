public class Store {
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;
    private final DeliveryRequestManager deliveryRequestManager;
    private final Inventory inventory;

    public Store() {
        this.accountManager = new AccountManager();
        this.transactionManager = new TransactionManager();
        this.deliveryRequestManager = new DeliveryRequestManager();
        this.inventory = new Inventory();
    }

    public void loadAll() {
        accountManager.loadAccounts();
        transactionManager.loadTransactions();
        deliveryRequestManager.loadDeliveryRequests();
        inventory.loadProducts();
    }

    public AccountManager getAccountManager() { return accountManager; }
    public TransactionManager getTransactionManager() { return transactionManager; }
    public DeliveryRequestManager getDeliveryRequestManager() { return deliveryRequestManager; }
    public Inventory getInventory() { return inventory; }
}
