package studentdatabase.task3.milestoneone;

abstract class PaymentFramework {

    protected double balance;
    protected final double VAT_RATE = 0.12;

    public PaymentFramework(double balance) {
        this.balance = balance;
    }

    public double processInvoice(double totalAmount, double discount) {
        System.out.println("Processing Guild Transaction...");

        try {
            if (totalAmount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero.");
            }

            if (discount < 0) {
                throw new IllegalArgumentException("Discount cannot be negative.");
            }

            double discountedAmount = applyDiscount(totalAmount, discount);
            double baseAmount = extractBaseAmount(discountedAmount);
            double vatAmount = calculateVATPortion(discountedAmount);

            displayBreakdown(baseAmount, vatAmount, discountedAmount);

            if (!validatePayment(discountedAmount)) {
                throw new IllegalStateException("Not enough gold for this transaction.");
            }

            finalizeTransaction(discountedAmount);

            return discountedAmount;

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Transaction Error: " + e.getMessage());
            return 0;

        } finally {
            System.out.println("Transaction attempt finished.\n");
        }
    }

    public boolean validatePayment(double amount) {
        return balance >= amount;
    }

    public double applyDiscount(double amount, double discount) {
        double result = amount - discount;

        if (result < 0) {
            throw new IllegalArgumentException("Discount exceeds total amount.");
        }

        return result;
    }

    public double extractBaseAmount(double totalAmount) {
        return totalAmount / (1 + VAT_RATE);
    }

    public double calculateVATPortion(double totalAmount) {
        return totalAmount - extractBaseAmount(totalAmount);
    }

    public void displayBreakdown(double base, double vat, double total) {
        System.out.println("---- Transaction Breakdown ----");
        System.out.println("Base Gold (VAT Exclusive): " + base);
        System.out.println("VAT (12%): " + vat);
        System.out.println("Total Gold Paid: " + total);
    }

    public void finalizeTransaction(double finalAmount) {
        deductGold(finalAmount);

        System.out.println("Transaction Complete!");
        System.out.println("Gold Deducted: " + finalAmount);
        System.out.println("Remaining Gold: " + balance);
    }

    protected void deductGold(double amount) {
        balance -= amount;
    }
}