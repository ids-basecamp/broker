# Upgrade version impacts
## _Impacts to upgrade Federated Catalog to EDC version 0.1.0_

Some identified issues bellow:

### Protocol migrated from IDS to DSP

The protocol used in components communication are not more [IDS](https://github.com/International-Data-Spaces-Association/ids-specification) protocol. The version 0.1.X is using the [DPS](https://docs.internationaldataspaces.org/dataspace-protocol/overview/readme) protocol.

Version milestone-8:
![image info](images/image-7.png)

Version 0.1.0:
![image info](images/image-8.png)

### FederatedCacheStore now receive a Catalog object:
We need to create the catalog object and refactor the methods to use it.

Image:

![image info](images/image-1.png)

### The Catalog object have new attributes:
We need to know if we will need to persist these new attributes and how.

Image:

![image info](images/image-2.png)

### Into Catalog object, ContractOffers was modified and the date attributes is deleted:
We use offerEnd attribute to expire the contractOffer, and it not exists anymore. 
We need to know how we will do the contract expiration and change our implementations.

Image:

![image info](images/image-3.png)

### The libraries structure was modified and some libraries does not exist anymore:

Image:

![image info](images/image-4.png)

### The way to parser was modified and the infomodel does not exist anymore. Now the response is a Map using Titanium library:
We need to change our parser implementation.

Image:

![image info](images/image-5.png)



