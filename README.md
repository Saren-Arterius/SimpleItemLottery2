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
* **/silot2 list [prize class default=1] [page default=1]** - Views prize list.
* **/silot2 make <prize class default=1> [ticket amount default=1]** - Makes lottery tickets.
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

Examples
========
You are an OP. 
-----------

Adding prizes
-----------
* Hold a diamond block in your hand, then type **/silot2 add 1 20** to add 1 diamond block as a prize to database.
* Hold a iron block in your hand, then type **/silot2 add 1 200** to add 1 iron block as a prize to database.
* Now, in prize class 1, diamond block and iron block have a relative probability of 20:200, which is 1:10 (9%:91%)
* Hold a dirt block in your hand, then type **/silot2 add 1 1000** to add 1 iron block as a prize to database.
* Now, in prize class 1, the blocks have a relative probability of 20:200:1000, which is 1:10:50 (1.6%:16.4%:82%)

* Hold a super awesome sword in your hand, then type **/silot2 add 2 5** to add it to class 2's prize list.
* And don't forget to type **/silot2 add 2 50 100000** to add $100000 as prize, 
* making it more difficult to get your super awesome sword.

* Got some even more rarer armors, and you want them to be prizes? 
* Type **/silot2 add 3 20** to add them to a even more higher class prize list!
* There is a hard limit of classes and probability number of 2147483647 which is 2^31 - 1.
* You can change the soft limit in config.yml.

Making lottery tickets
-----------
* Type **/silot2 make 1 16** to make 16 * class 1 lottery tickets. 
* You can consider selling them in regular shops, or give them to newcomers.

* Type **/silot2 make 2** to make a class 2 lottery tickets. 
* Consider selling them in higher class shop or giving them out as event prizes!

* Type **/silot2 make 3** to make a class 3 lottery tickets. 
* Consider letting them be one of the drops of a boss mob.

Deleting prizes
-----------
* You found the super awesome sword not appropriate in a class 2 prize list, let's delete it.
* You know that it's a class 2 prize, but you apparently don't know which row ID represents the sword.
* First, type **/silot2 list 2** to locate what the sword's row ID is, 
* then you type **/silot2 delete <row ID>** to delete it from the list.

You are a Player. 
-----------

Using tickets
-----------
* You bought some tickets from an admin shop, or you just got them in an event.
* The only thing you have to do now is to use them.
* Trust me, it's very simple to use, you just right click on them and see what you got.
