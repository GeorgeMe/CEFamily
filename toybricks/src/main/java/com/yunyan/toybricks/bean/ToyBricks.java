package com.yunyan.toybricks.bean;

import java.util.List;

public class ToyBricks {
	
	private List<ToyBricksAdvertisement> Advertisements;
	private List<ToyBricksProduct> Products;
	
	public List<ToyBricksAdvertisement> getAdvertisements() {
		return Advertisements;
	}

	public void setAdvertisements(List<ToyBricksAdvertisement> advertisements) {
		Advertisements = advertisements;
	}

	public List<ToyBricksProduct> getProducts() {
		return Products;
	}

	public void setProducts(List<ToyBricksProduct> products) {
		Products = products;
	}

}
