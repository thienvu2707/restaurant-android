package com.android.thienvu.restaurants.Database;

public class DatabaseItem {

    private final String userID;
    private final String restaurantName;
    private final String location;
    private final String price;
    private final int rating;
    private final String image;

    public DatabaseItem(String userID, String restaurantName, String location, String price, int rating, String image)
    {
        this.userID = userID;
        this.restaurantName = restaurantName;
        this.location = location;
        this.price = price;
        this.rating = rating;

        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public int getRating() {
        return rating;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getUserID() {
        return userID;
    }

    @Override
    public String toString() {
        return "ReviewItem{" +
                "userID='" + userID + '\'' +
                ", restaurant='" + restaurantName +'\'' +
                ", location='" + location + '\'' +
                ", price='" + price +'\'' +
                ", rating=" +rating + '}';
    }
}

