Price Comparator

A backend service for comparing product prices across multiple Romanian grocery stores like Lidl, Kaufland, and Profi. This solution enables users to monitor prices, track discounts, optimize their shopping baskets, and receive email alerts when product prices drop below a desired threshold.

✅ Features Implemented

- Daily Shopping Basket Optimization: Splits a user's shopping list into the best deals across available stores.

- Top Discounts: Returns a list of the most discounted items (by percentage).

- New Discounts: Identifies and displays discounts added in the last 24 hours.

- Dynamic Price History: Returns a timeline of price changes (with and without discounts) for any product, filterable by store, category, or brand.

- Price Alerts: Users can create alerts for specific products and get notified by email when their price drops to the target.

⚙️ Technology Stack

- Language: Java 17

- Framework: Spring Boot

- Build Tool: Maven

- Database: MySQL

- CSV Parsing: OpenCSV

- Email: Spring Mail (Mailtrap used for testing)

📁 Project Structure Overview

- controllers/: API endpoints

- services/: Business logic

- models/: Entities (Product, Discount, Alert, etc.)

- repositories/: Spring Data JPA for persistence

- dtos/: Data transfer objects for API responses

- mappers/: Converts model objects to DTOs'

- scheduling/: Schedule alert checks

- utils/: Utility methods (e.g., unit conversions, price rounding)


📊  Sample API Usage

1. Importing Data from CSV

  *Products*
  - POST http://localhost:8080/import/products
  - Form-data: file = Lidl_2025-05-01.csv

  *Discounts*
  - POST http://localhost:8080/import/discounts
  - Form-data: file = Lidl_discounts_2025-05-01.csv

2. View Current & Top Discounts

   *Current Discounts*
   - GET http://localhost:8080/analytics/currentDiscounts
  
   *New Discounts (today/ yesterday)*
   - GET http://localhost:8080/analytics/currentDiscounts
  
   *Top 10 Discounts (default is 5)*
   - GET http://localhost:8080/analytics/topDiscounts?limit=5

3. Optimize Basket

   *Cheapest Product and Store for Each Item*
   - POST http://localhost:8080/optimizeBasket/
   - Body: ["lapte zuzu","spaghetti nr.5","ciocolată neagră 70%"]
  
   *Optimized List Grouped by Store*
   - POST http://localhost:8080/optimizeBasket/byStore
   - Body: ["lapte zuzu","spaghetti nr.5","ciocolată neagră 70%"]

4. Price History

   *Full History (returns Price History Points for Each Store in Order)*
   - GET http://localhost:8080/priceHistory/?productName=lapte%20zuzu

   *Filtered by Store and Category*
   - GET http://localhost:8080/priceHistory/?productName=lapte%20zuzu&storeName=Kaufland&category=lactate
   
5. Price Alerts

   *Create Alert*
   - GET http://localhost:8080/alerts?email=test@gmail.com&productName=lapte zuzu&targetPrice=7

   *Trigger Alert Check (manual)*
   - GET http://localhost:8080/alerts/check

🧠 Assumptions & Design Decisions

- CSV filenames follow the pattern: store_yyyy-MM-dd.csv or store_discounts_yyyy-MM-dd.csv.

- The same product_name string is assumed to identify the same product across stores.

- Units like g and ml are normalized to kg and l for value comparison.

- Products and discounts are saved with dateAdded derived from the filename.

- Alerts are persisted and checked on a schedule (can be run manually too).

- Assumed only one discount can be active at a store per product.

- Price per unit logic helps recommend better deals even across different package sizes.

⚠️ Security Notice:

- Previous commits were rewritten to remove accidentally committed credentials. No confidential information is currently present in the repository.


