package com.inso.modules.coin.contract.processor.factory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import org.tron.tronj.client.TronClient;

import java.util.List;
import java.util.Map;

public class TronNodeWrapper {

    private static Log LOG = LogFactory.getLog(TronNodeWrapper.class);

    private static final String DEFAULT_OWNER_PRIVATE_KEY = "773ac2f9c388b4ff8c7654013cff0fc6ecc103a9f2928b38ee8f5525c1e113ca";
    private static final String DEFAULT_OWNER_ADDRESS = "TLVpywaFM4ryBx5fxY4xNb79P1nHXC5JGM";

    private CryptoNetworkType mNetworkType;

    private Map<String, String> mKeyMaps = Maps.newHashMap();
    private List<MyTronClient> mClientList = Lists.newArrayList();

    private int mSize = 0;
    private int currentIndex = 0;

    public TronNodeWrapper(CryptoNetworkType networkType)
    {
        this.mNetworkType = networkType;

        init();
    }

    private TronClient mWriteClient;

    public TronClient getWriterClient()
    {
        return mWriteClient;
    }

    private void init()
    {
        if(mNetworkType == CryptoNetworkType.TRX_GRID)
        {
            this.mWriteClient = TronClient.ofMainnet(DEFAULT_OWNER_PRIVATE_KEY);
        }
        else if(mNetworkType == CryptoNetworkType.TRX_NILE)
        {
            this.mWriteClient = TronClient.ofNile(DEFAULT_OWNER_PRIVATE_KEY);
        }

        for(String apiServer : mNetworkType.getBackupApiServerList())
        {
            if(mKeyMaps.containsKey(apiServer))
            {
                continue;
            }

            MyTronClient  client = new MyTronClient(apiServer);
            mClientList.add(client);

            mKeyMaps.put(apiServer, StringUtils.getEmpty());
        }
        this.mSize = mNetworkType.getBackupApiServerList().size();
    }

    public TronClient getNode()
    {
        int nextIndex = this.currentIndex;
        if(nextIndex >= mSize)
        {
            nextIndex = 0;
            this.currentIndex = 0;
        }
        else
        {
            this.currentIndex ++;
        }

        TronClient client =  mClientList.get(nextIndex).getClient();

        //LOG.info("current select node index = " + nextIndex + ", mSize = " + mSize);
        return client;
    }

    private class MyTronClient {

        private TronClient mClient;

        private String mNodeIp;

        public MyTronClient(String nodeIP)
        {
            this.mNodeIp = nodeIP;
        }


        public TronClient getClient()
        {
            if(mClient != null)
            {
                return mClient;
            }

            synchronized (TronNodeWrapper.class)
            {
                if(mClient != null)
                {
                    return mClient;
                }

                if(mNetworkType == CryptoNetworkType.TRX_GRID)
                {
                    if(mNodeIp.startsWith("http"))
                    {
                        mClient = TronClient.ofMainnet(DEFAULT_OWNER_PRIVATE_KEY);
                    }
                    else
                    {
                        mClient = new TronClient(mNodeIp + ":50051", mNodeIp + ":50052", DEFAULT_OWNER_PRIVATE_KEY);
                    }

                }
                else if(mNetworkType == CryptoNetworkType.TRX_NILE)
                {
                    mClient = TronClient.ofNile(DEFAULT_OWNER_PRIVATE_KEY);
                }
            }

            return mClient;
        }

    }

    public static void main(String[] args) {
        TronNodeWrapper nodeWrapper = new TronNodeWrapper(CryptoNetworkType.TRX_GRID);
        nodeWrapper.getNode();
        nodeWrapper.getNode();
        nodeWrapper.getNode();
    }


}
