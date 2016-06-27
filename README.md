# Sample event sourced application with Command Query Responsibility Segregation

** Event sourcing **

Shop item can be bought, paid, and marked as payment timeout. Aggregate root (ShopItem) emits 3 different types of domain events: ItemBought, ItemPaid, ItemPaymentMissing. All of them are consequences of commands.

Event store is constructed in database as EventStream table with collection of EventDescriptors. EventStream is fetched by unique aggregate root uuid.

** CQRS **

Read model is constructed by listening to domain events mentioned before. This task is performed by ReadModelOnDomainEventUpdater