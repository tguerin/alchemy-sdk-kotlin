//
//  ContentView.swift
//  NftExplorer
//
//  Created by Thomas GUERIN on 09/01/2023.
//

import SwiftUI
import alchemy

struct ContentView: View {
    
    @StateObject var vm = NftsViewModel()
    
    var body: some View {
        VStack {
            HStack {
                TextField("Enter your nft contract address", text: $vm.nftAddress)
                Button(action: {
                    Task {
                        await vm.getNfts()
                    }
                }){ Text("Ok").padding() }
            }
            List{
                ForEach(vm.nfts){nft in
                    NftItemView(nft: nft)
                }
            }
            .listStyle(PlainListStyle())
            .navigationTitle("Nfts")
        }
        .padding()
    }
}

struct NftItem: Identifiable, Codable {
    let id: Int
    let imageUrl: String
}

struct NftItemView: View {
    var nft: NftItem
    
    var body: some View {
        HStack{
            AsyncImage(
                url:  URL(string: nft.imageUrl)!,
              content: { image in
              image
                .resizable()
                .aspectRatio(contentMode: .fit)
            }, placeholder: {
              Color.gray
            })
              .frame(width: 100, height: 100)
              .mask(RoundedRectangle(cornerRadius: 16))
        }
    }
}

@MainActor
class NftsViewModel: ObservableObject {
    
    private var alchemy: Alchemy = Alchemy.companion.with(alchemySettings: AlchemySettings(apiKey: "Your API key", network: Network.ethMainnet, url: nil, wsSettings: AlchemySettings.WsSettings(retryPolicy: DelayRetryPolicy(delayMs: 10000))))
    
    @Published var nfts: [NftItem] = []
    @Published var nftAddress: String = ""
    
    
    func getNfts() async {
        await withCheckedContinuation { continuation in
            print(nftAddress)
            alchemy.nft.getNftsForContract(contractAddress: Address.companion.from(rawAddress: nftAddress), options: GetNftsForContractOptions(startToken: nil, limit: nil, omitMetadata: false, tokenUriTimeoutInMs: nil)) { nfts, error in
                print(nfts?.isSuccess == true)
                let nftItems = nfts?.getOrNull()?.nfts.map {
                    NftItem(id: Int($0.tokenId.intValue()), imageUrl: ($0 as! Nft_AlchemyNft).media[0].gateway!)
                }
                DispatchQueue.main.async{
                    self.nfts = (nftItems == nil) ? [] : nftItems!
                    continuation.resume()
                }
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
