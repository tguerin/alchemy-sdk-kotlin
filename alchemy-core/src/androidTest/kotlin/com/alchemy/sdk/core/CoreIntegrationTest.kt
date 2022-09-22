package com.alchemy.sdk.core

import androidx.annotation.IdRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.alchemy.sdk.core.model.Address
import com.alchemy.sdk.core.model.AlchemySettings
import com.alchemy.sdk.core.model.Block
import com.alchemy.sdk.core.model.BlockCount.Companion.blockCount
import com.alchemy.sdk.core.model.BlockTag
import com.alchemy.sdk.core.model.BlockTransaction
import com.alchemy.sdk.core.model.FeeHistory
import com.alchemy.sdk.core.model.GasEstimation
import com.alchemy.sdk.core.model.Index.Companion.index
import com.alchemy.sdk.core.model.Log
import com.alchemy.sdk.core.model.LogFilter
import com.alchemy.sdk.core.model.Network
import com.alchemy.sdk.core.model.Percentile.Companion.percentile
import com.alchemy.sdk.core.model.Proof
import com.alchemy.sdk.core.model.TransactionReceipt
import com.alchemy.sdk.core.model.UncleBlock
import com.alchemy.sdk.core.test.R
import com.alchemy.sdk.core.util.Ether.Companion.wei
import com.alchemy.sdk.core.util.GsonUtil
import com.alchemy.sdk.core.util.HexString.Companion.hexString
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldHaveSize
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStreamReader
import java.math.BigInteger

@RunWith(AndroidJUnit4::class)
class CoreIntegrationTest {

    private val alchemy = Alchemy.with(AlchemySettings(network = Network.ETH_MAINNET))

    private val gson = GsonUtil.get()

    @Test
    fun getBalance() = runTest {
        val result =
            alchemy.core.getBalance(Address.from("0x1188aa75C38E1790bE3768508743FBE7b50b2153"))
        result.getOrThrow() shouldBeEqualTo "0x3529b5834ea3c6".hexString.wei
    }

    @Test
    fun getCode() = runTest {
        val data = alchemy.core.getCode(
            address = Address.from("0x4B076f0E07eED3F1007fB1B5C000F7A08D3208E1")
        )
        data.getOrThrow()
            .toString() shouldBeEqualTo "0x6080604052600436106102345760003560e01c80638b83209b1161012e578063c4ae3168116100ab578063d79779b21161006f578063d79779b2146106f1578063e222c7f914610727578063e33b7de31461073c578063e985e9c514610751578063f2fde38b1461077157600080fd5b8063c4ae31681461065b578063c668286214610670578063c87b56dd14610685578063ce7c2ac2146106a5578063d5abeb01146106db57600080fd5b8063a035b1fe116100f2578063a035b1fe146105d3578063a22cb465146105e9578063a45063c014610609578063b3ab66b014610628578063b88d4fde1461063b57600080fd5b80638b83209b146105345780638d8fd771146105545780638da5cb5b1461056a57806395d89b41146105885780639852595c1461059d57600080fd5b80632a55205a116101bc57806348b750441161018057806348b75044146104a55780635c975abb146104c55780636352211e146104df57806370a08231146104ff578063715018a61461051f57600080fd5b80632a55205a146103cb5780633a98ef391461040a5780633f87db251461041f578063406072a91461043f57806342842e0e1461048557600080fd5b80631680a148116102035780631680a1481461033357806318160ddd14610348578063191655871461036b57806321670f221461038b57806323b872dd146103ab57600080fd5b806301ffc9a71461028257806306fdde03146102b7578063081812fc146102d9578063095ea7b31461031157600080fd5b3661027d577f6ef95f06320e7a25a04a175ca677b7052bdd97131872c2192525a629f51be77033604080516001600160a01b0390921682523460208301520160405180910390a1005b600080fd5b34801561028e57600080fd5b506102a261029d36600461245c565b610791565b60405190151581526020015b60405180910390f35b3480156102c357600080fd5b506102cc6107e3565b6040516102ae91906124d1565b3480156102e557600080fd5b506102f96102f43660046124e4565b610875565b6040516001600160a01b0390911681526020016102ae565b34801561031d57600080fd5b5061033161032c366004612512565b61090f565b005b34801561033f57600080fd5b506102cc610a24565b34801561035457600080fd5b5061035d610ab2565b6040519081526020016102ae565b34801561037757600080fd5b5061033161038636600461253e565b610ac2565b34801561039757600080fd5b506103316103a6366004612512565b610bf3565b3480156103b757600080fd5b506103316103c636600461255b565b610cf0565b3480156103d757600080fd5b506103eb6103e636600461259c565b610d21565b604080516001600160a01b0390931683526020830191909152016102ae565b34801561041657600080fd5b5060085461035d565b34801561042b57600080fd5b5061033161043a36600461264a565b610daa565b34801561044b57600080fd5b5061035d61045a366004612693565b6001600160a01b039182166000908152600e6020908152604080832093909416825291909152205490565b34801561049157600080fd5b506103316104a036600461255b565b610deb565b3480156104b157600080fd5b506103316104c0366004612693565b610e06565b3480156104d157600080fd5b506013546102a29060ff1681565b3480156104eb57600080fd5b506102f96104fa3660046124e4565b610fe2565b34801561050b57600080fd5b5061035d61051a36600461253e565b611059565b34801561052b57600080fd5b506103316110e0565b34801561054057600080fd5b506102f961054f3660046124e4565b611116565b34801561056057600080fd5b5061035d60145481565b34801561057657600080fd5b506006546001600160a01b03166102f9565b34801561059457600080fd5b506102cc611146565b3480156105a957600080fd5b5061035d6105b836600461253e565b6001600160a01b03166000908152600b602052604090205490565b3480156105df57600080fd5b5061035d60155481565b3480156105f557600080fd5b506103316106043660046126da565b611155565b34801561061557600080fd5b506013546102a290610100900460ff1681565b6103316106363660046124e4565b611160565b34801561064757600080fd5b50610331610656366004612708565b6113f0565b34801561066757600080fd5b50610331611422565b34801561067c57600080fd5b506102cc611460565b34801561069157600080fd5b506102cc6106a03660046124e4565b61146d565b3480156106b157600080fd5b5061035d6106c036600461253e565b6001600160a01b03166000908152600a602052604090205490565b3480156106e757600080fd5b5061035d60105481565b3480156106fd57600080fd5b5061035d61070c36600461253e565b6001600160a01b03166000908152600d602052604090205490565b34801561073357600080fd5b506103316115ce565b34801561074857600080fd5b5060095461035d565b34801561075d57600080fd5b506102a261076c366004612693565b611615565b34801561077d57600080fd5b5061033161078c36600461253e565b6116d5565b60006001600160e01b031982166380ac58cd60e01b14806107c257506001600160e01b03198216635b5e139f60e01b145b806107dd57506301ffc9a760e01b6001600160e01b03198316145b92915050565b6060600080546107f290612788565b80601f016020809104026020016040519081016040528092919081815260200182805461081e90612788565b801561086b5780601f106108405761010080835404028352916020019161086b565b820191906000526020600020905b81548152906001019060200180831161084e57829003601f168201915b5050505050905090565b6000818152600260205260408120546001600160a01b03166108f35760405162461bcd60e51b815260206004820152602c60248201527f4552433732313a20617070726f76656420717565727920666f72206e6f6e657860448201526b34b9ba32b73a103a37b5b2b760a11b60648201526084015b60405180910390fd5b506000908152600460205260409020546001600160a01b031690565b600061091a82610fe2565b9050806001600160a01b0316836001600160a01b0316036109875760405162461bcd60e51b815260206004820152602160248201527f4552433732313a20617070726f76616c20746f2063757272656e74206f776e656044820152603960f91b60648201526084016108ea565b336001600160a01b03821614806109a357506109a38133611615565b610a155760405162461bcd60e51b815260206004820152603860248201527f4552433732313a20617070726f76652063616c6c6572206973206e6f74206f7760448201527f6e6572206e6f7220617070726f76656420666f7220616c6c000000000000000060648201526084016108ea565b610a1f8383611770565b505050565b60118054610a3190612788565b80601f0160208091040260200160405190810160405280929190818152602001828054610a5d90612788565b8015610aaa5780601f10610a7f57610100808354040283529160200191610aaa565b820191906000526020600020905b815481529060010190602001808311610a8d57829003601f168201915b505050505081565b6000610abd60165490565b905090565b6001600160a01b0381166000908152600a6020526040902054610af75760405162461bcd60e51b81526004016108ea906127c2565b6000610b0260095490565b610b0c904761281e565b90506000610b398383610b34866001600160a01b03166000908152600b602052604090205490565b6117de565b905080600003610b5b5760405162461bcd60e51b81526004016108ea90612836565b6001600160a01b0383166000908152600b602052604081208054839290610b8390849061281e565b925050819055508060096000828254610b9c919061281e565b90915550610bac9050838261181c565b604080516001600160a01b0385168152602081018390527fdf20fd1e76bc69d672e4814fafb2c449bba3a5369d8359adf9e05e6fde87b056910160405180910390a1505050565b6006546001600160a01b03163314610c1d5760405162461bcd60e51b81526004016108ea90612881565b6000610c2860165490565b905060008211610c735760405162461bcd60e51b815260206004820152601660248201527513185b991cd8d85c194e881e995c9bc8185b5bdd5b9d60521b60448201526064016108ea565b601054610c80828461281e565b1115610cc45760405162461bcd60e51b8152602060048201526013602482015272098c2dcc8e6c6c2e0ca7440e8dede40daeac6d606b1b60448201526064016108ea565b60005b82811015610cea57610cd884611935565b80610ce2816128b6565b915050610cc7565b50505050565b610cfa33826119ba565b610d165760405162461bcd60e51b81526004016108ea906128cf565b610a1f838383611a90565b60008281526002602052604081205481906001600160a01b0316610d875760405162461bcd60e51b815260206004820152601860248201527f4c616e6473636170653a206e6f7468696e67207468657265000000000000000060448201526064016108ea565b816064610d95856005612920565b610d9f9190612955565b915091509250929050565b6006546001600160a01b03163314610dd45760405162461bcd60e51b81526004016108ea90612881565b8051610de79060119060208401906123ad565b5050565b610a1f838383604051806020016040528060008152506113f0565b6001600160a01b0381166000908152600a6020526040902054610e3b5760405162461bcd60e51b81526004016108ea906127c2565b6001600160a01b0382166000908152600d60205260408120546040516370a0823160e01b81523060048201526001600160a01b038516906370a0823190602401602060405180830381865afa158015610e98573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250810190610ebc9190612969565b610ec6919061281e565b90506000610eff8383610b3487876001600160a01b039182166000908152600e6020908152604080832093909416825291909152205490565b905080600003610f215760405162461bcd60e51b81526004016108ea90612836565b6001600160a01b038085166000908152600e6020908152604080832093871683529290529081208054839290610f5890849061281e565b90915550506001600160a01b0384166000908152600d602052604081208054839290610f8590849061281e565b90915550610f969050848483611c2c565b604080516001600160a01b038581168252602082018490528616917f3be5b7a71e84ed12875d241991c70855ac5817d847039e17a9d895c1ceb0f18a910160405180910390a250505050565b6000818152600260205260408120546001600160a01b0316806107dd5760405162461bcd60e51b815260206004820152602960248201527f4552433732313a206f776e657220717565727920666f72206e6f6e657869737460448201526832b73a103a37b5b2b760b91b60648201526084016108ea565b60006001600160a01b0382166110c45760405162461bcd60e51b815260206004820152602a60248201527f4552433732313a2062616c616e636520717565727920666f7220746865207a65604482015269726f206164647265737360b01b60648201526084016108ea565b506001600160a01b031660009081526003602052604090205490565b6006546001600160a01b0316331461110a5760405162461bcd60e51b81526004016108ea90612881565b6111146000611c7e565b565b6000600c828154811061112b5761112b612982565b6000918252602090912001546001600160a01b031692915050565b6060600180546107f290612788565b610de7338383611cd0565b3332146111a45760405162461bcd60e51b81526020600482015260126024820152712737ba1030b63637bbb2b21037b934b3b4b760711b60448201526064016108ea565b601354610100900460ff166111fb5760405162461bcd60e51b815260206004820152601c60248201527f4c616e6473636170653a205075626c696353616c65206973204f46460000000060448201526064016108ea565b60135460ff161561124e5760405162461bcd60e51b815260206004820152601d60248201527f4c616e6473636170653a20436f6e74726163742069732070617573656400000060448201526064016108ea565b600081116112975760405162461bcd60e51b815260206004820152601660248201527513185b991cd8d85c194e881e995c9bc8185b5bdd5b9d60521b60448201526064016108ea565b6014548111156112fa5760405162461bcd60e51b815260206004820152602860248201527f4c616e6473636170653a20596f752063616e2774206d696e7420736f206d75636044820152676820746f6b656e7360c01b60648201526084016108ea565b600061130560165490565b601054909150611315838361281e565b11156113635760405162461bcd60e51b815260206004820152601e60248201527f4c616e6473636170653a204d617820737570706c79206578636565646564000060448201526064016108ea565b34826015546113729190612920565b11156113ca5760405162461bcd60e51b815260206004820152602160248201527f4c616e6473636170653a204e6f7420656e6f756768206574686572732073656e6044820152601d60fa1b60648201526084016108ea565b60005b82811015610a1f576113de33611935565b806113e8816128b6565b9150506113cd565b6113fa33836119ba565b6114165760405162461bcd60e51b81526004016108ea906128cf565b610cea84848484611d9e565b6006546001600160a01b0316331461144c5760405162461bcd60e51b81526004016108ea90612881565b6013805460ff19811660ff90911615179055565b60128054610a3190612788565b6000818152600260205260409020546060906001600160a01b03166114ec5760405162461bcd60e51b815260206004820152602f60248201527f4552433732314d657461646174613a2055524920717565727920666f72206e6f60448201526e3732bc34b9ba32b73a103a37b5b2b760891b60648201526084016108ea565b6000601180546114fb90612788565b80601f016020809104026020016040519081016040528092919081815260200182805461152790612788565b80156115745780601f1061154957610100808354040283529160200191611574565b820191906000526020600020905b81548152906001019060200180831161155757829003601f168201915b50505050509050600081511161159957604051806020016040528060008152506115c7565b806115a384611dd1565b60126040516020016115b793929190612998565b6040516020818303038152906040525b9392505050565b6006546001600160a01b031633146115f85760405162461bcd60e51b81526004016108ea90612881565b6013805461ff001981166101009182900460ff1615909102179055565b600f5460405163c455279160e01b81526001600160a01b03848116600483015260009281169190841690829063c455279190602401602060405180830381865afa158015611667573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525081019061168b9190612a5b565b6001600160a01b0316036116a35760019150506107dd565b6001600160a01b0380851660009081526005602090815260408083209387168352929052205460ff165b949350505050565b6006546001600160a01b031633146116ff5760405162461bcd60e51b81526004016108ea90612881565b6001600160a01b0381166117645760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b60648201526084016108ea565b61176d81611c7e565b50565b600081815260046020526040902080546001600160a01b0319166001600160a01b03841690811790915581906117a582610fe2565b6001600160a01b03167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92560405160405180910390a45050565b6008546001600160a01b0384166000908152600a6020526040812054909183916118089086612920565b6118129190612955565b6116cd9190612a78565b8047101561186c5760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a20696e73756666696369656e742062616c616e636500000060448201526064016108ea565b6000826001600160a01b03168260405160006040518083038185875af1925050503d80600081146118b9576040519150601f19603f3d011682016040523d82523d6000602084013e6118be565b606091505b5050905080610a1f5760405162461bcd60e51b815260206004820152603a60248201527f416464726573733a20756e61626c6520746f2073656e642076616c75652c207260448201527f6563697069656e74206d6179206861766520726576657274656400000000000060648201526084016108ea565b6002600754036119875760405162461bcd60e51b815260206004820152601f60248201527f5265656e7472616e637947756172643a207265656e7472616e742063616c6c0060448201526064016108ea565b600260075561199a601680546001019055565b60006119a560165490565b90506119b18282611ed2565b50506001600755565b6000818152600260205260408120546001600160a01b0316611a335760405162461bcd60e51b815260206004820152602c60248201527f4552433732313a206f70657261746f7220717565727920666f72206e6f6e657860448201526b34b9ba32b73a103a37b5b2b760a11b60648201526084016108ea565b6000611a3e83610fe2565b9050806001600160a01b0316846001600160a01b03161480611a655750611a658185611615565b806116cd5750836001600160a01b0316611a7e84610875565b6001600160a01b031614949350505050565b826001600160a01b0316611aa382610fe2565b6001600160a01b031614611b075760405162461bcd60e51b815260206004820152602560248201527f4552433732313a207472616e736665722066726f6d20696e636f72726563742060448201526437bbb732b960d91b60648201526084016108ea565b6001600160a01b038216611b695760405162461bcd60e51b8152602060048201526024808201527f4552433732313a207472616e7366657220746f20746865207a65726f206164646044820152637265737360e01b60648201526084016108ea565b611b74600082611770565b6001600160a01b0383166000908152600360205260408120805460019290611b9d908490612a78565b90915550506001600160a01b0382166000908152600360205260408120805460019290611bcb90849061281e565b909155505060008181526002602052604080822080546001600160a01b0319166001600160a01b0386811691821790925591518493918716917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef91a4505050565b604080516001600160a01b038416602482015260448082018490528251808303909101815260649091019091526020810180516001600160e01b031663a9059cbb60e01b179052610a1f908490611eec565b600680546001600160a01b038381166001600160a01b0319831681179093556040519116919082907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a35050565b816001600160a01b0316836001600160a01b031603611d315760405162461bcd60e51b815260206004820152601960248201527f4552433732313a20617070726f766520746f2063616c6c65720000000000000060448201526064016108ea565b6001600160a01b03838116600081815260056020908152604080832094871680845294825291829020805460ff191686151590811790915591519182527f17307eab39ab6107e8899845ad3d59bd9653f200f220920489ca2b5937696c31910160405180910390a3505050565b611da9848484611a90565b611db584848484611fbe565b610cea5760405162461bcd60e51b81526004016108ea90612a8f565b606081600003611df85750506040805180820190915260018152600360fc1b602082015290565b8160005b8115611e225780611e0c816128b6565b9150611e1b9050600a83612955565b9150611dfc565b60008167ffffffffffffffff811115611e3d57611e3d6125be565b6040519080825280601f01601f191660200182016040528015611e67576020820181803683370190505b5090505b84156116cd57611e7c600183612a78565b9150611e89600a86612ae1565b611e9490603061281e565b60f81b818381518110611ea957611ea9612982565b60200101906001600160f81b031916908160001a905350611ecb600a86612955565b9450611e6b565b610de78282604051806020016040528060008152506120bf565b6000611f41826040518060400160405280602081526020017f5361666545524332303a206c6f772d6c6576656c2063616c6c206661696c6564815250856001600160a01b03166120f29092919063ffffffff16565b805190915015610a1f5780806020019051810190611f5f9190612af5565b610a1f5760405162461bcd60e51b815260206004820152602a60248201527f5361666545524332303a204552433230206f7065726174696f6e20646964206e6044820152691bdd081cdd58d8d9595960b21b60648201526084016108ea565b60006001600160a01b0384163b156120b457604051630a85bd0160e11b81526001600160a01b0385169063150b7a0290612002903390899088908890600401612b12565b6020604051808303816000875af192505050801561203d575060408051601f3d908101601f1916820190925261203a91810190612b4f565b60015b61209a573d80801561206b576040519150601f19603f3d011682016040523d82523d6000602084013e612070565b606091505b5080516000036120925760405162461bcd60e51b81526004016108ea90612a8f565b805181602001fd5b6001600160e01b031916630a85bd0160e11b1490506116cd565b506001949350505050565b6120c98383612101565b6120d66000848484611fbe565b610a1f5760405162461bcd60e51b81526004016108ea90612a8f565b60606116cd8484600085612243565b6001600160a01b0382166121575760405162461bcd60e51b815260206004820181905260248201527f4552433732313a206d696e7420746f20746865207a65726f206164647265737360448201526064016108ea565b6000818152600260205260409020546001600160a01b0316156121bc5760405162461bcd60e51b815260206004820152601c60248201527f4552433732313a20746f6b656e20616c7265616479206d696e7465640000000060448201526064016108ea565b6001600160a01b03821660009081526003602052604081208054600192906121e590849061281e565b909155505060008181526002602052604080822080546001600160a01b0319166001600160a01b03861690811790915590518392907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef908290a45050565b6060824710156122a45760405162461bcd60e51b815260206004820152602660248201527f416464726573733a20696e73756666696369656e742062616c616e636520666f6044820152651c8818d85b1b60d21b60648201526084016108ea565b6001600160a01b0385163b6122fb5760405162461bcd60e51b815260206004820152601d60248201527f416464726573733a2063616c6c20746f206e6f6e2d636f6e747261637400000060448201526064016108ea565b600080866001600160a01b031685876040516123179190612b6c565b60006040518083038185875af1925050503d8060008114612354576040519150601f19603f3d011682016040523d82523d6000602084013e612359565b606091505b5091509150612369828286612374565b979650505050505050565b606083156123835750816115c7565b8251156123935782518084602001fd5b8160405162461bcd60e51b81526004016108ea91906124d1565b8280546123b990612788565b90600052602060002090601f0160209004810192826123db5760008555612421565b82601f106123f457805160ff1916838001178555612421565b82800160010185558215612421579182015b82811115612421578251825591602001919060010190612406565b5061242d929150612431565b5090565b5b8082111561242d5760008155600101612432565b6001600160e01b03198116811461176d57600080fd5b60006020828403121561246e57600080fd5b81356115c781612446565b60005b8381101561249457818101518382015260200161247c565b83811115610cea5750506000910152565b600081518084526124bd816020860160208601612479565b601f01601f19169290920160200192915050565b6020815260006115c760208301846124a5565b6000602082840312156124f657600080fd5b5035919050565b6001600160a01b038116811461176d57600080fd5b6000806040838503121561252557600080fd5b8235612530816124fd565b946020939093013593505050565b60006020828403121561255057600080fd5b81356115c7816124fd565b60008060006060848603121561257057600080fd5b833561257b816124fd565b9250602084013561258b816124fd565b929592945050506040919091013590565b600080604083850312156125af57600080fd5b50508035926020909101359150565b634e487b7160e01b600052604160045260246000fd5b600067ffffffffffffffff808411156125ef576125ef6125be565b604051601f8501601f19908116603f01168101908282118183101715612617576126176125be565b8160405280935085815286868601111561263057600080fd5b858560208301376000602087830101525050509392505050565b60006020828403121561265c57600080fd5b813567ffffffffffffffff81111561267357600080fd5b8201601f8101841361268457600080fd5b6116cd848235602084016125d4565b600080604083850312156126a657600080fd5b82356126b1816124fd565b915060208301356126c1816124fd565b809150509250929050565b801515811461176d57600080fd5b600080604083850312156126ed57600080fd5b82356126f8816124fd565b915060208301356126c1816126cc565b6000806000806080858703121561271e57600080fd5b8435612729816124fd565b93506020850135612739816124fd565b925060408501359150606085013567ffffffffffffffff81111561275c57600080fd5b8501601f8101871361276d57600080fd5b61277c878235602084016125d4565b91505092959194509250565b600181811c9082168061279c57607f821691505b6020821081036127bc57634e487b7160e01b600052602260045260246000fd5b50919050565b60208082526026908201527f5061796d656e7453706c69747465723a206163636f756e7420686173206e6f2060408201526573686172657360d01b606082015260800190565b634e487b7160e01b600052601160045260246000fd5b6000821982111561283157612831612808565b500190565b6020808252602b908201527f5061796d656e7453706c69747465723a206163636f756e74206973206e6f742060408201526a191d59481c185e5b595b9d60aa1b606082015260800190565b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b6000600182016128c8576128c8612808565b5060010190565b60208082526031908201527f4552433732313a207472616e736665722063616c6c6572206973206e6f74206f6040820152701ddb995c881b9bdc88185c1c1c9bdd9959607a1b606082015260800190565b600081600019048311821515161561293a5761293a612808565b500290565b634e487b7160e01b600052601260045260246000fd5b6000826129645761296461293f565b500490565b60006020828403121561297b57600080fd5b5051919050565b634e487b7160e01b600052603260045260246000fd5b6000845160206129ab8285838a01612479565b8551918401916129be8184848a01612479565b8554920191600090600181811c90808316806129db57607f831692505b85831081036129f857634e487b7160e01b85526022600452602485fd5b808015612a0c5760018114612a1d57612a4a565b60ff19851688528388019550612a4a565b60008b81526020902060005b85811015612a425781548a820152908401908801612a29565b505083880195505b50939b9a5050505050505050505050565b600060208284031215612a6d57600080fd5b81516115c7816124fd565b600082821015612a8a57612a8a612808565b500390565b60208082526032908201527f4552433732313a207472616e7366657220746f206e6f6e20455243373231526560408201527131b2b4bb32b91034b6b83632b6b2b73a32b960711b606082015260800190565b600082612af057612af061293f565b500690565b600060208284031215612b0757600080fd5b81516115c7816126cc565b6001600160a01b0385811682528416602082015260408101839052608060608201819052600090612b45908301846124a5565b9695505050505050565b600060208284031215612b6157600080fd5b81516115c781612446565b60008251612b7e818460208701612479565b919091019291505056fea2646970667358221220cb0d3cdcafba99b06dd8da255daa6484dcf8c2b696a0109f8d3473869e4f754e64736f6c634300080e0033"
    }

    @Test
    fun getStorageAt() = runTest {
        val data = alchemy.core.getStorageAt(
            address = Address.from("0x4B076f0E07eED3F1007fB1B5C000F7A08D3208E1"),
            index = 0.index
        )
        data.getOrThrow() shouldBeEqualTo "0x41494c616e647363617065000000000000000000000000000000000000000016".hexString
    }

    @Test
    fun getProof() = runTest {
        val data = alchemy.core.getProof(
            address = Address.from("0x4B076f0E07eED3F1007fB1B5C000F7A08D3208E1"),
            keys = listOf("0x41494c616e647363617065000000000000000000000000000000000000000016".hexString)
        )
        val expectedProof = gson.fromJson<Proof?>(
            jsonReaderFromFileName(R.raw.proof_test),
            Proof::class.java
        )
        data.getOrThrow().address shouldBeEqualTo expectedProof.address
        data.getOrThrow().accountProof shouldHaveSize expectedProof.accountProof.size
        data.getOrThrow().balance shouldBeEqualTo expectedProof.balance
        data.getOrThrow().codeHash shouldBeEqualTo expectedProof.codeHash
        data.getOrThrow().nonce shouldBeEqualTo expectedProof.nonce
        data.getOrThrow().storageHash shouldBeEqualTo expectedProof.storageHash
        data.getOrThrow().storageProof shouldBeEqualTo expectedProof.storageProof
    }

    @Test
    fun getProtocolVersion() = runTest {
        val data = alchemy.core.getProtocolVersion()
        data.getOrThrow().decimalValue() shouldBeEqualTo BigInteger("65")
    }

    @Test
    fun getChainId() = runTest {
        val data = alchemy.core.getChainId()
        data.getOrThrow() shouldBeEqualTo "0x1".hexString
    }

    @Test
    fun getNetListening() = runTest {
        val data = alchemy.core.getNetListening()
        data.getOrThrow() shouldBeEqualTo true
    }

    @Test
    fun getNetVersion() = runTest {
        val data = alchemy.core.getNetVersion()
        data.getOrThrow() shouldBeEqualTo "1"
    }

    @Test
    fun getWeb3ClientVersion() = runTest {
        val data = alchemy.core.getWeb3ClientVersion()
        data.getOrThrow() shouldBeEqualTo "Geth/v1.10.23-stable-d901d853/linux-amd64/go1.18.5"
    }

    @Test
    fun getWeb3Sha3() = runTest {
        val data = alchemy.core.getWeb3Sha3("0x68656c6c6f20776f726c64".hexString)
        data.getOrThrow() shouldBeEqualTo "0x47173285a8d7341e5e972fc677286384f802f8ef42a5ec5f03bbfa254cb01fad".hexString
    }

    @Test
    fun getAccounts() = runTest {
        val data = alchemy.core.getAccounts()
        data.getOrThrow() shouldBeEqualTo emptyList()
    }

    @Test
    fun getBlockNumber() = runTest {
        val data = alchemy.core.getBlockNumber()
        data.getOrThrow() // No way to check the value, we just check we have a result
    }

    @Test
    @FlakyTest
    fun getBlockByNumber() = runTest {
        val blockTag = BlockTag.BlockTagNumber("0xed14e5".hexString)

        val data = alchemy.core.getBlockByNumber(blockTag, true)

        val expectedBlock = gson.fromJson<Block?>(
            jsonReaderFromFileName(R.raw.block_test),
            Block::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlock
    }

    @Test
    @FlakyTest
    fun getBlockByHash() = runTest {
        val blockHash =
            "0x4e216c95f527e9ba0f1161a1c4609b893302c704f05a520da8141ca91878f63e".hexString

        val data = alchemy.core.getBlockByHash(blockHash, true)

        val expectedBlock = gson.fromJson<Block?>(
            jsonReaderFromFileName(R.raw.block_test),
            Block::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlock
    }

    @Test
    @FlakyTest
    fun getBlockByHashWithoutTransactions() = runTest {
        val blockHash =
            "0x4e216c95f527e9ba0f1161a1c4609b893302c704f05a520da8141ca91878f63e".hexString

        val data = alchemy.core.getBlockByHash(blockHash)

        val expectedBlock = gson.fromJson<Block?>(
            jsonReaderFromFileName(R.raw.block_without_transactions_test),
            Block::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlock
    }

    @Test
    fun getBlockTransactionCountByNumber() = runTest {
        val blockTag = BlockTag.BlockTagNumber("0xed14e5".hexString)

        val data = alchemy.core.getBlockTransactionCountByNumber(blockTag)

        data.getOrThrow() shouldBeEqualTo "0xf8".hexString
    }

    @Test
    fun getBlockTransactionCountByHash() = runTest {
        val data = alchemy.core.getBlockTransactionCountByHash(
            "0x4e216c95f527e9ba0f1161a1c4609b893302c704f05a520da8141ca91878f63e".hexString
        )

        data.getOrThrow() shouldBeEqualTo "0xf8".hexString
    }

    @Test
    fun getUncleByBlockNumberAndIndex() = runTest {
        val blockTag = BlockTag.BlockTagNumber("0xed14e5".hexString)

        val data = alchemy.core.getUncleByBlockNumberAndIndex(blockTag, 0.index)

        val expectedBlock = gson.fromJson<UncleBlock?>(
            jsonReaderFromFileName(R.raw.uncle_block_test),
            UncleBlock::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlock
    }

    @Test
    fun getUncleByBlockHashAndIndex() = runTest {
        val data = alchemy.core.getUncleByBlockHashAndIndex(
            "0x4e216c95f527e9ba0f1161a1c4609b893302c704f05a520da8141ca91878f63e".hexString,
            0.index
        )

        val expectedBlock = gson.fromJson<UncleBlock?>(
            jsonReaderFromFileName(R.raw.uncle_block_test),
            UncleBlock::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlock
    }

    @Test
    fun getUncleCountByBlockNumber() = runTest {
        val blockTag = BlockTag.BlockTagNumber("0xed14e5".hexString)

        val data = alchemy.core.getUncleCountByBlockNumber(blockTag)

        data.getOrThrow().intValue() shouldBeEqualTo 1
    }

    @Test
    fun getUncleCountByBlockHash() = runTest {
        val data = alchemy.core.getUncleCountByBlockHash(
            "0x4e216c95f527e9ba0f1161a1c4609b893302c704f05a520da8141ca91878f63e".hexString
        )
        data.getOrThrow().intValue() shouldBeEqualTo 1
    }

    @Test
    fun getTransactionByBlockNumberAndIndex() = runTest {
        val blockTag = BlockTag.BlockTagNumber("0xed14e5".hexString)

        val data = alchemy.core.getTransactionByBlockNumberAndIndex(blockTag, 0.index)

        val expectedBlockTransaction = gson.fromJson<BlockTransaction?>(
            jsonReaderFromFileName(R.raw.transaction_test),
            BlockTransaction::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlockTransaction
    }

    @Test
    fun getTransactionByBlockHashAndIndex() = runTest {
        val data = alchemy.core.getTransactionByBlockHashAndIndex(
            "0x4e216c95f527e9ba0f1161a1c4609b893302c704f05a520da8141ca91878f63e".hexString,
            0.index
        )

        val expectedBlockTransaction = gson.fromJson<BlockTransaction?>(
            jsonReaderFromFileName(R.raw.transaction_test),
            BlockTransaction::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlockTransaction
    }

    @Test
    fun getTransactionCount() = runTest {
        val data = alchemy.core.getTransactionCount(
            Address.from("0x10ce4cd51b9e95be1c8a9bc665d3ebdfa9762529"),
            BlockTag.BlockTagNumber("0xed14e5".hexString)
        )

        data.getOrThrow().intValue() shouldBeEqualTo 6185
    }

    @Test
    fun getTransactionByHash() = runTest {
        val data = alchemy.core.getTransactionByHash(
            "0x6576804cb20d1bab7898d22eaf4fed6fec75ddaf43ef43b97f2c8011e449deef".hexString
        )

        val expectedBlockTransaction = gson.fromJson<BlockTransaction?>(
            jsonReaderFromFileName(R.raw.transaction_test),
            BlockTransaction::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlockTransaction
    }

    @Test
    fun getTransactionReceipt() = runTest {
        val data = alchemy.core.getTransactionReceipt(
            "0x6576804cb20d1bab7898d22eaf4fed6fec75ddaf43ef43b97f2c8011e449deef".hexString
        )

        val expectedBlockTransaction = gson.fromJson<TransactionReceipt?>(
            jsonReaderFromFileName(R.raw.transaction_receipt_test),
            TransactionReceipt::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedBlockTransaction
    }

    @Test
    @FlakyTest // Returns 503 for now...
    fun estimateGas() = runTest {
        val data = alchemy.core.estimateGas(listOf(GasEstimation.BlockTagGasEstimation(BlockTag.Latest)))
        data.getOrThrow().decimalValue() shouldBeGreaterThan BigInteger.valueOf(0)
    }


    @Test
    fun getGasPrice() = runTest {
        val data = alchemy.core.getGasPrice()
        data.getOrThrow().gigaWei.toDouble() shouldBeGreaterThan 0.0
    }

    @Test
    fun getMaxPriorityFeePerGas() = runTest {
        val data = alchemy.core.getMaxPriorityFeePerGas()
        data.getOrThrow().gigaWei.toDouble() shouldBeGreaterThan 0.0
    }

    @Test
    fun getFeeHistoryWithoutPercentiles() = runTest {
        val data = alchemy.core.getFeeHistory(
            4.blockCount,
            BlockTag.BlockTagNumber("0xed14e5".hexString)
        )
        val expectedFeeHistory = gson.fromJson<FeeHistory?>(
            jsonReaderFromFileName(R.raw.fee_history_test),
            FeeHistory::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedFeeHistory
    }

    @Test
    fun getFeeHistoryWithPercentiles() = runTest {
        val data = alchemy.core.getFeeHistory(
            4.blockCount,
            BlockTag.BlockTagNumber("0xed14e5".hexString),
            listOf(25.percentile, 75.percentile)
        )
        val expectedFeeHistory = gson.fromJson<FeeHistory?>(
            jsonReaderFromFileName(R.raw.fee_history_percentiles_test),
            FeeHistory::class.java
        )
        data.getOrThrow() shouldBeEqualTo expectedFeeHistory
    }

    @Test
    fun getLogsWithBlockHash() = runTest {
        val data = alchemy.core.getLogs(
            LogFilter.BlockHashFilter(
                "0x40c3019758abf6942b29d5efb43d0c26abac7db3c8545232b8a3bdf37c780dc1".hexString
            )
        )

        val expectedLogs = gson.fromJson<Array<Log>>(
            jsonReaderFromFileName(R.raw.logs_test),
            Array<Log>::class.java
        ).toList()

        data.getOrThrow() shouldBeEqualTo expectedLogs
    }

    private fun jsonReaderFromFileName(@IdRes fileRes: Int): JsonReader {
        return JsonReader(
            InputStreamReader(
                getInstrumentation().context.resources.openRawResource(fileRes)
            )
        )
    }
}