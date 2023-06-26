# Upgrade version impacts
## _Impacts to upgrade federated catalog to EDC version 0.1.0_

Some identified issues bellow:

### FederatedCacheStore now receive a Catalog object:
We need to create the catalog object and refactor the methods to use it.

Image:

![image info](./issues-img-temp/image-1.png)

### The Catalog object have new attributes:
We need to know if we will use one of this attributes.

Image:

![image info](./issues-img-temp/image-2.png)

### Into Catalog object, ContractOffers was modified and the date attributes is deleted:
We use offerEnd attribute to expire the contractOffer, and it not exists anymore. 
We need to know how we will do the contract expiration and change our implementations.

Image:

![image info](./issues-img-temp/image-3.png)

### The libraries structure was modified and some libraries does not exist anymore:

Image:

![image info](./issues-img-temp/image-4.png)

### The way to parser was modified and the infomodel does not exist anymore. Now the response is a Map using Titanium library:
We need to change our parser implementation.

Image:

![image info](./issues-img-temp/image-5.png)



