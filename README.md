# Markets

**Market Trial** is a simple market manager. With thorough api, it is a a showcase and practice using MongoDB transactions to create a very secure market system that is easily expandable.

## Features
- **Completely Customizable**: Every message and GUI are 100% customizable.
- **MongoDB**: Items are stored on a mongo database and is completely safe to link multiple servers!
- **Blackmarket**: Rarely, an item is put onto the black market for sale at a discounted price. 
- **Automatic Saving**: Data is safely stored in the database frequently.
- **Smooth GUIs**: The marketplace updates are live and can be seen without reopening the window!

### Command Structure
- `/marketplace`  
  *View items currently for sell. Permission: marketplace.view*
  
- `/blackmarket`  
  *View blackmarket items currently for sell. Refreshes daily with content from the market. Permission: marketplace.blackmarket*

- `/sell [amount]`  
  *List an item as for sell in the market. Permission: marketplace.sell*

- `/transactions`  
  *View the transaction history of a player.*
