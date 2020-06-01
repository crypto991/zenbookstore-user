package com.bookstore.controller;

import com.bookstore.domain.*;
import com.bookstore.service.*;
import com.bookstore.utility.WorldCountryConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Controller
public class CheckoutController {

    private ShippingAddress shippingAddress = new ShippingAddress();
    private BillingAddress billingAddress = new BillingAddress();
    private Payment payment = new Payment();

    private UserService userService;
    private CartItemService cartItemService;
    private ShippingAddressService shippingAddressService;
    private PaymentService paymentService;
    private BillingAddressService billingAddressService;
    private UserShippingService userShippingService;
    private UserPaymentService userPaymentService;
    private OrderService orderService;
    private ShoppingCartService shoppingCartService;
    private EmailService emailService;

    public CheckoutController(UserService userService, CartItemService cartItemService,
                              ShippingAddressService shippingAddressService, PaymentService paymentService,
                              BillingAddressService billingAddressService, UserShippingService userShippingService,
                              UserPaymentService userPaymentService, OrderService orderService,
                              ShoppingCartService shoppingCartService, EmailService emailService) {
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.shippingAddressService = shippingAddressService;
        this.paymentService = paymentService;
        this.billingAddressService = billingAddressService;
        this.userShippingService = userShippingService;
        this.userPaymentService = userPaymentService;
        this.orderService = orderService;
        this.shoppingCartService = shoppingCartService;
        this.emailService = emailService;
    }

    @RequestMapping("/checkout")
    public String checkout(
            @RequestParam("id") Long cartId,
            @RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField,
            Model model, Principal principal
    ) {
        User user = userService.findByUsername(principal.getName());

        if (cartId != user.getShoppingCart().getId()) {
            return "badRequestPage";
        }

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

        if (cartItemList.size() == 0) {
            model.addAttribute("emptyCart", true);
            return "forward:/shoppintCart/cart";
        }

        for (CartItem cartItem : cartItemList) {
            if (cartItem.getBook().getInStockNumber() < cartItem.getQty()) {
                model.addAttribute("notEnoughStock", true);
                return "forward:/shoppingCart/cart";
            }
        }

        List<UserShipping> userShippingList = user.getUserShippingList();
        List<UserPayment> userPaymentList = user.getUserPaymentList();

        model.addAttribute("userShippingList", userShippingList);
        model.addAttribute("userPaymentList", userPaymentList);

        if (userPaymentList.size() == 0) {
            model.addAttribute("emptyPaymentList", true);
        } else {
            model.addAttribute("emptyPaymentList", false);
        }

        if (userShippingList.size() == 0) {
            model.addAttribute("emptyShippingList", true);
        } else {
            model.addAttribute("emptyShippingList", false);
        }

        ShoppingCart shoppingCart = user.getShoppingCart();

        for (UserShipping userShipping : userShippingList) {
            if (userShipping.isUserShippingDefault()) {
                shippingAddressService.setByUserShipping(userShipping, shippingAddress);
            }
        }

        for (UserPayment userPayment : userPaymentList) {
            if (userPayment.isDefaultPayment()) {
                paymentService.setByUserPayment(userPayment, payment);
                billingAddressService.setByUserBilling(userPayment.getUserBilling(), billingAddress);
            }
        }

        model.addAttribute("shippingAddress", shippingAddress);
        model.addAttribute("payment", payment);
        model.addAttribute("billingAddress", billingAddress);
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("shoppingCart", user.getShoppingCart());

        List<String> stateList = WorldCountryConstants.listOfWorldCountryCodes;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);

        model.addAttribute("classActiveShipping", true);

        if (missingRequiredField) {
            model.addAttribute("missingRequiredField", true);
        }

        return "checkout";

    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String checkout(
            @ModelAttribute("shippingAddress") ShippingAddress shippingAddress,
            @ModelAttribute("billingAddress") BillingAddress billingAddress,
            @ModelAttribute("payment") Payment payment,
            @ModelAttribute("billingSameAsShipping") String billingSameAsShipping,
            @ModelAttribute("shippingMethod") String shippingMethod,
            Principal principal,
            Model model
    ) {
        ShoppingCart shoppingCart = userService.findByUsername(principal.getName()).getShoppingCart();

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

        if (billingSameAsShipping.equals("true")) {
            billingAddressService.setByShippingAddress(shippingAddress, billingAddress);
        }

        if (shippingAddress.getShippingAddressStreet1().isEmpty() || shippingAddress.getShippingAddressCity().isEmpty()
                || shippingAddress.getShippingAddressCountry().isEmpty()
                || shippingAddress.getShippingAddressName().isEmpty()
                || shippingAddress.getShippingAddressZipCode().isEmpty() || payment.getCardNumber().isEmpty()
                || payment.getCvc() == 0 || billingAddress.getBillingAddressStreet1().isEmpty()
                || billingAddress.getBillingAddressCity().isEmpty() || billingAddress.getBillingAddressCountry().isEmpty()
                || billingAddress.getBillingAddressName().isEmpty()
                || billingAddress.getBillingAddressZipCode().isEmpty())
            return "redirect:/checkout?id=" + shoppingCart.getId() + "&missingRequiredField=true";

        User user = userService.findByUsername(principal.getName());
        Order order = orderService.createOrder(shoppingCart, shippingAddress, billingAddress, payment, shippingMethod, user);

        try {
            emailService.sendOrderMail(user, order, Locale.ITALY);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        shoppingCartService.clearShoppingCart(shoppingCart);

        LocalDate today = LocalDate.now();
        LocalDate estimatedDeliveryDate;

        if (shippingMethod.equals("groundShipping")) {
            estimatedDeliveryDate = today.plusDays(5);
        } else {
            estimatedDeliveryDate = today.plusDays(3);
        }


        model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);
        model.addAttribute("cartItemList", cartItemList);


        return "orderSubmittedPage";
    }


    @RequestMapping("/setShippingAddress")
    public String setShippingAddress(
            @RequestParam("userShippingId") Long userShippingId,
            Principal principal, Model model
    ) {
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(userShippingId);

        if (userShipping.getUser().getId() != user.getId()) {
            return "badRequestPage";
        } else {
            shippingAddressService.setByUserShipping(userShipping, shippingAddress);
            List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());
            BillingAddress billingAddress = new BillingAddress();

            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("payment", payment);
            model.addAttribute("billingAddress", billingAddress);
            model.addAttribute("cartItemList", cartItemList);
            model.addAttribute("shoppingCart", user.getShoppingCart());

            List<String> stateList = WorldCountryConstants.listOfWorldCountryCodes;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);


            List<UserShipping> userShippingList = user.getUserShippingList();
            List<UserPayment> userPaymentList = user.getUserPaymentList();

            model.addAttribute("userShippingList", userShippingList);
            model.addAttribute("userPaymentList", userPaymentList);

            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("classActiveShipping", true);

            if (userPaymentList.size() == 0) {
                model.addAttribute("emptyPaymentList", true);
            } else {
                model.addAttribute("emptyPaymentList", false);
            }


            model.addAttribute("emptyShippingList", false);

            return "checkout";

        }


    }

    @RequestMapping("/setPaymentMethod")
    public String setPaymentMethod(
            @RequestParam("userPaymentId") Long userPaymentId,
            Principal principal, Model model
    ) {
        User user = userService.findByUsername(principal.getName());
        UserPayment userPayment = userPaymentService.findById(userPaymentId);
        UserBilling userBilling = userPayment.getUserBilling();


        if (userPayment.getUser().getId() != user.getId()) {
            return "badRequestPage";
        } else {
            paymentService.setByUserPayment(userPayment, payment);

            List<CartItem> cartItemList = cartItemService.findByShoppingCart(user.getShoppingCart());

            billingAddressService.setByUserBilling(userBilling, billingAddress);

            model.addAttribute("shippingAddress", shippingAddress);
            model.addAttribute("payment", payment);
            model.addAttribute("billingAddress", billingAddress);
            model.addAttribute("cartItemList", cartItemList);
            model.addAttribute("shoppingCart", user.getShoppingCart());

            List<String> stateList = WorldCountryConstants.listOfWorldCountryCodes;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            List<UserShipping> userShippingList = user.getUserShippingList();
            List<UserPayment> userPaymentList = user.getUserPaymentList();

            model.addAttribute("userShippingList", userShippingList);
            model.addAttribute("userPaymentList", userPaymentList);

            model.addAttribute("shippingAddress", shippingAddress);

            model.addAttribute("classActivePayment", true);


            model.addAttribute("emptyPaymentList", false);


            if (userShippingList.size() == 0) {
                model.addAttribute("emptyShippingList", true);
            } else {
                model.addAttribute("emptyShippingList", false);
            }

            return "checkout";
        }
    }


}
