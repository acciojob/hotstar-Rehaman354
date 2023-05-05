package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User newUser=new User();
        newUser.setSubscription(user.getSubscription());
        newUser.setName(user.getName());
        newUser.setAge(user.getAge());
        newUser.setMobNo(user.getMobNo());
        User savedUser=userRepository.save(newUser);
        return savedUser.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user=userRepository.findById(userId).get();
        SubscriptionType userSubscriptionType=user.getSubscription().getSubscriptionType();
        int count=0;
        for(WebSeries webSeries: webSeriesRepository.findAll())
        {
            if(user.getAge()>=webSeries.getAgeLimit()) {
                //if the age of user is equal to greater than webseries
                if(userSubscriptionType==SubscriptionType.ELITE) {//all elite candidates can watch
                    count++;
                }
                else if(userSubscriptionType==SubscriptionType.PRO){
                    if(webSeries.getSubscriptionType()==SubscriptionType.BASIC||webSeries.getSubscriptionType()==SubscriptionType.PRO)
                        count++;
                }
                else if(userSubscriptionType==SubscriptionType.BASIC){
                    if(webSeries.getSubscriptionType()==SubscriptionType.BASIC)count++;
                }
            }
        }
        return count;
    }


}
