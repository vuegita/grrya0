package com.inso.modules.ad.mall.logical;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.google.common.collect.Lists;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.modules.ad.core.model.AdEventOrderInfo;
import com.inso.modules.ad.mall.service.MallDeliveryService;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.paychannel.helper.EmailPhoneHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class DeliveryLocationManager {

    private static Log LOG = LogFactory.getLog(DeliveryLocationManager.class);

    @Autowired
    private MallDeliveryService mallDeliveryService;

    public List<String> getLocationList(long userid)
    {
        // 1. 已下单
        // 2. 已发货
        // 3. 已揽件
        // 4. Shipping
        // 5.
        List<String> rsList = Lists.newArrayList();
        rsList.add("Order Processing (Product Packaging)");
        rsList.add("Shipping");
        rsList.add("Arrive at the Street Transit Center");
        rsList.add("On the way to delivery");
        rsList.add("Arrival (Completed)");
        return rsList;
    }

    public void addDeliveryLocation(AdEventOrderInfo orderInfo, OrderTxStatus shippingStatus)
    {
        if(orderInfo.getUserid() > 0)
        {
            return;
        }

        if(shippingStatus != OrderTxStatus.PENDING)
        {
            return;
        }

        mallDeliveryService.batchAdd(orderInfo.getNo(), getLocationList(orderInfo.getUserid()));
    }

    public static void main(String[] args) {
        Faker faker = new Faker(Locale.US);

        String name = faker.name().fullName();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();

        String streetAddress = faker.address().streetAddress();

        System.out.println(faker.address().zipCode());

        String split = " - ";
        for(int i = 0; i < 100 ; i ++)
        {
            Address address = faker.address();
            String rs = address.streetAddress() + split + address.city() + split + address.country() + split;
//            System.out.println(faker.address().streetAddress());
//            System.out.println(faker.address().city());
//            System.out.println(faker.address().country());
//            System.out.println(rs);
        }


    }

}
