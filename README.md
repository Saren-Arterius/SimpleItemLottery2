SimpleItemLottery2
==================

Craftbukkit plugin. Lottery tickets to item prizes or cash prizes. Supports item meta.

Usage
========
* OPs define a prize list, then make lottery tickets, then give them to players.
* Players right click the tickets to randomly get a prize (item or cash) from the prize list of a prize class.

Feature
========
* Class of prizes and lottery tickets
* Relative probability
* Support items of anykind
* Optional econ (Vault) support

Commands
========
* **/silot2 add <prize class> <probabilty> [money amount]** - Hold an item on hand, and type the command to add it as prize into database. If money amount is givin, cash prize will be added instead.
* **/silot2 list [prize class] [page] ** - Views prize list.
* **/silot2 make <prize class> [ticket amount] ** - Makes lottery tickets.
* **/silot2 delete (id 1, id 2, id 3...)** - Deletes prize rows from database.
* **/silot2 reload** - Reloads the plugin.

Permissions
========

    SILOT2.*:
        description: Gives access to all SimpleItemLottery2 commands.
        default: op
    SILOT2.admin:
        description: Gives access to all SimpleItemLottery2 administrative commands.
        children:
            SILOT2.reload: true
            SILOT2.list: true
            SILOT2.use: true
            SILOT2.class.*: true
        default: op
    SILOT2.reload:
        description: Reloads the plugin.
        default: op
    SILOT2.list:
        description: Players with this permission can list all the prizes.
        default: op
    SILOT2.use:
        description: Gives access to use lottery ticket.
        default: true
    SILOT2.class.*:
        description: Gives access to use class X lottery ticket.
        default: true
