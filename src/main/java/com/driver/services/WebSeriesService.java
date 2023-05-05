package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        String seriesName= webSeriesEntryDto.getSeriesName();
        if(webSeriesRepository.findBySeriesName(seriesName)!=null)
            throw new Exception("Series is already present");
        WebSeries webSeries=new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        ProductionHouse productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        webSeries.setProductionHouse(productionHouse);
        WebSeries savedWebseries=webSeriesRepository.save(webSeries);
        //after saving updating production attributes
        double cuurentRating=productionHouse.getRatings();
        int noOfWebseries=productionHouse.getWebSeriesList().size();
        double newRating=((noOfWebseries*cuurentRating)+webSeriesEntryDto.getRating())/(noOfWebseries+1);
        productionHouse.setRatings(newRating);
        productionHouse.getWebSeriesList().add(savedWebseries);
        productionHouseRepository.save(productionHouse);
        return savedWebseries.getId();
    }

}
