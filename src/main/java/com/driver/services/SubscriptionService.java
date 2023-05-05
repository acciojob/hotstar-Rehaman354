package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        int userId=subscriptionEntryDto.getUserId();
        User user=userRepository.findById(userId).get();
        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setStartSubscriptionDate(new Date());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        //setting amountPaid
        int amount;
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            amount=500+(200*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
        {
            amount=800+(250*subscriptionEntryDto.getNoOfScreensRequired());
        }
        else
        {
            amount=1000+(350* subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(amount);
        subscription.setUser(user);
        Subscription s=subscriptionRepository.save(subscription);
        user.setSubscription(s);
        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        int diffAmount;
       if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE))
           throw new Exception("Already the best Subscription");
       else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO))
       {
           int currentAmount=user.getSubscription().getTotalAmountPaid();
           Subscription subscription=user.getSubscription();
           subscription.setSubscriptionType(SubscriptionType.ELITE);
           subscription.setStartSubscriptionDate(new Date());
           int newAmount= 1000+(350*subscription.getNoOfScreensSubscribed());
           subscription.setTotalAmountPaid(newAmount);
           Subscription s=subscriptionRepository.save(subscription);
           user.setSubscription(s);
           userRepository.save(user);
           diffAmount=newAmount-currentAmount;
       }
       else {
           int currentAmount=user.getSubscription().getTotalAmountPaid();
           Subscription subscription=user.getSubscription();
           subscription.setSubscriptionType(SubscriptionType.PRO);
           subscription.setStartSubscriptionDate(new Date());
           int newAmount= 800+(250*subscription.getNoOfScreensSubscribed());
           subscription.setTotalAmountPaid(newAmount);
           Subscription s=subscriptionRepository.save(subscription);
           user.setSubscription(s);
           userRepository.save(user);
           diffAmount=newAmount-currentAmount;

       }
        return diffAmount;
    }

    @SuppressWarnings("ReassignedVariable")
    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
       int ans = 0;
       for(Subscription s: subscriptionRepository.findAll()){
           ans+=s.getTotalAmountPaid();
       }
        return ans;
    }

}
