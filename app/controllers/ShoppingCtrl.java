package controllers;

import play.mvc.*;
import play.data.*;
import javax.inject.Inject;

import views.html.*;
import play.db.ebean.Transactional;
import play.api.Environment;

// Import models
import models.users.*;
import models.products.*;
import models.shopping.*;

// Import security controllers
import controllers.security.*;

@Security.Authenticated(Secured.class)
@With(CheckIfCustomer.class)

public class ShoppingCtrl extends Controller {


    /** Dependency Injection **/

    /** http://stackoverflow.com/questions/15600186/play-framework-dependency-injection **/
    private FormFactory formFactory;

    /** http://stackoverflow.com/a/37024198 **/
    private Environment env;

    /** http://stackoverflow.com/a/10159220/6322856 **/
    @Inject
    public ShoppingCtrl(Environment e, FormFactory f) {
        this.env = e;
        this.formFactory = f;
    }


    
    // Get a user - if logged in email will be set in the session
	private Customer getCurrentUser() {
		return (Customer)User.getLoggedIn(session().get("email"));
	}


    
    @Transactional
    public Result showBasket(){
        return ok(basket.render(getCurrentUser()));
    }
    

    @Transactional
    public Result addToBasket(Long id){
        Product p = Product.find.byId(id);
        Customer customer = (Customer)User.getLoggedIn(session().get("email"));

        if (customer.getBasket() == null) {
            customer.setBasket(new Basket());
            customer.getBasket().setCustomer(customer);
            customer.update();
        }
        customer.getBasket().addProduct(p);
        customer.update();
        return ok(basket.render(customer));
    }

    // Empty Basket
    @Transactional
    public Result emptyBasket() {
        
        Customer c = getCurrentUser();
        c.getBasket().removeAllItems();
        c.getBasket().update();
        
        return ok(basket.render(c));
    }

  @Transactional
  public Result addOne(Long itemId){
      OrderItem item = OrderItem.find.byId(itemId);
      item.increaseQty();
      item.update();
      return redirect(routes.ShoppingCtrl.showBasket());
  }
    @Transactional
    public Result removeOne(Long itemId){
        OrderItem item = OrderItem.find.byId(itemId);
        Customer c = getCurrentUser();
        c.getBasket().removeItem(item);
        c.getBasket().update();
        return ok(basket.render(c));
    }
    // View an individual order
    @Transactional
    public Result viewOrder(long id) {
        ShopOrder order = ShopOrder.find.byId(id);
        return ok(orderConfirmed.render(getCurrentUser(), order));
    }

    public Result placeOrder(){
        Customer c = getCurrentUser();
        ShopOrder order = new ShopOrder();
        order.setCustomer(c);


        order.setItems(c.getBasket().getBasketItems());

        order.save();

        for(OrderItem i: order.getItems()){
            i.setOrder(order);
            i.setBasket(null);
            i.update();
        }
        order.update();
        c.getBasket().setBasketItems(null);
        c.getBasket().update();
        return ok(orderConfirmed.render(c,order));

    }

}