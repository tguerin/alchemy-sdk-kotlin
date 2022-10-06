# Alchemy Sdk Kotlin

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/tguerin/alchemy-sdk-kotlin/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/tguerin/alchemy-sdk-kotlin/tree/main)

Support Alchemy on Android devices and java based projects. This is a side project to learn stuff around web3 and releasing a library.

## Roadmap

- [x] core api
- [x] nft api
- [x] ens support
- [x] transact api (missing wait for transaction that requires ws)
- [ ] websocket api
- [ ] wallet api

The API is subject to heavy changes in the upcoming releases

## Getting started

Add the github repository to your gradle config:

```kotlin
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/tguerin/alchemy-sdk-kotlin")
        credentials {
            username = "username"
            password = "personal access token with read permission"
        }
    }
}
```

then just add the dependency to the required module:

```kotlin
implementation("com.github.tguerin:alchemy-sdk-kotlin:0.6.0")
```

## Using Alchemy sdk

This Alchemy sdk relies on coroutines, the api is quite straightforward:

### Core

```kotlin
val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

coroutineScope.launch {
    val result = alchemy.core.getBalance(Address.from("0x1188aa75c38e1790be3768508743fbe7b50b2153"))
    val balance = result.getOrElse { 
        // handle failure
    }
}
```

### Websocket

The websocket api returns a ```Flow```:

```kotlin
val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

coroutineScope.launch {
    alchemy.ws.on(WebsocketMethod.Block).collect { event ->
        // handle data
    }
    alchemy.ws.status().collect { connectionStatus ->
        // You can display if you are connected or not to the websocket
    }
}
```

The websocket will automatically close if no subscriber is registered.

Have a look at the [e2e tests](./alchemy-core/src/test/kotlin/com/alchemy/sdk/core/e2e) for samples.

No benchmark has been made but the init can take some time. The alchemy object is thread safe so 
you can do the init off the main thread.


