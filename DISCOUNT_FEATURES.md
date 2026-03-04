# GUI Discount Management Features

## Overview
The Coffee Shop Management System GUI has been enhanced with comprehensive discount management capabilities. Users can now create, view, and delete three types of discounts.

## New Features

### 1. Orders Tab (Original)
- Manage customers and their orders
- Calculate bills with applied discounts
- Manage individual customer transactions

### 2. Manage Discounts Tab (New)
A dedicated interface for managing all available discounts in the system.

## Discount Types

### 1. **Percentage Discount**
- Apply a percentage discount to a specific item
- Example: 10% off on Espresso
- Configuration:
  - Select Target Item
  - Percentage (0.0 - 1.0, e.g., 0.1 = 10%)

### 2. **X for Y Discount**
- Buy X items, get Y at a discount
- Example: Buy 3 Cappuccinos, get the 4th one free
- Configuration:
  - Select Target Item
  - X (quantity to buy)
  - Y (quantity to discount)
  - Constraint: Y must be less than X

### 3. **Meal Deal**
- Bundle multiple items at a fixed price
- Example: Coffee + Croissant + Juice for £5.99
- Configuration:
  - Select multiple items (checkbox list)
  - Set fixed meal deal price

## Usage Instructions

### Creating a Discount

1. Open the application and navigate to the **"Manage Discounts"** tab
2. Choose one of the discount type buttons:
   - **Percentage Discount** - For item discounts
   - **Meal Deal** - For bundled items
   - **X for Y Discount** - For quantity-based promotions

3. Fill in the required fields
4. Click **Create** to add the discount

### Viewing Discounts

The "Manage Discounts" tab displays:
- **Discount Type**: The class name (DiscountPercentage, DiscountX4X, DiscountMealDeal)
- **Details**: Human-readable description of the discount
  - Percentage Discount: "ITEM_ID: 10% off"
  - X for Y: "ITEM_ID: Buy 3, get 1 free"
  - Meal Deal: "Items: ITEM1, ITEM2 -> £5.99"

### Deleting a Discount

1. Select a discount from the table in the "Manage Discounts" tab
2. Click **Delete Selected Discount**
3. Confirm the deletion

### Refreshing the List

- Click the **Refresh** button to reload the discount list from the system

## Integration with Order Processing

When calculating bills, the system automatically:
1. Detects which discounts apply to the customer's orders
2. Applies discounts in evaluation order
3. Calculates the final bill with all applicable discounts

The "Orders" tab shows the applied discounts when you click **Calculate Bill**.

## Data Persistence

All discounts are automatically saved to `data/discounts.csv` when you:
- Click **Save Data** in the Orders tab
- Close out a customer

Discounts are loaded from the CSV file when the application starts.

## Error Handling

The system validates discount parameters:
- **Percentage Discount**: Percentage must be > 0 and < 1
- **X for Y**: Both X and Y must be > 0, and Y must be < X
- **Meal Deal**: List must have at least one item, cost must be > 0

Invalid inputs will display error messages in dialog boxes.
