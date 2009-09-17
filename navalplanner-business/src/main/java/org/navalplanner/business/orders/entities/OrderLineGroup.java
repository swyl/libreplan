package org.navalplanner.business.orders.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.Valid;
import org.navalplanner.business.advance.entities.AdvanceAssigment;

public class OrderLineGroup extends OrderElement implements IOrderLineGroup {

    public static OrderLineGroup create() {
        OrderLineGroup result = new OrderLineGroup();
        result.setNewObject(true);
        return result;
    }

    private List<OrderElement> children = new ArrayList<OrderElement>();

    /**
     * Constructor for hibernate. Do not use!
     */
    public OrderLineGroup() {

    }

    @Override
    @Valid
    public List<OrderElement> getChildren() {
        return new ArrayList<OrderElement>(children);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public void remove(OrderElement child) {
        getManipulator().remove(child);
    }

    @Override
    public void replace(OrderElement oldOrderElement, OrderElement orderElement) {
        getManipulator().replace(oldOrderElement, orderElement);
    }

    @Override
    public void add(OrderElement orderElement) {
        getManipulator().add(orderElement);
    }

    @Override
    public void up(OrderElement orderElement) {
        getManipulator().up(orderElement);
    }

    private OrderLineGroupManipulator getManipulator() {
        return OrderLineGroupManipulator.createManipulatorForOrderLineGroup(
                this, children);
    }

    @Override
    public OrderLine toLeaf() {
        OrderLine result = OrderLine.create();

        result.setName(getName());
        result.setInitDate(getInitDate());
        result.setEndDate(getEndDate());
        result.setWorkHours(0);

        return result;
    }

    @Override
    public OrderLineGroup toContainer() {
        return this;
    }

    @Override
    public void down(OrderElement orderElement) {
        getManipulator().down(orderElement);
    }

    @Override
    public void add(int position, OrderElement orderElement) {
        children.add(position, orderElement);
    }

    @Override
    public Integer getWorkHours() {
        int result = 0;
        List<OrderElement> children = getChildren();
        for (OrderElement orderElement : children) {
            result += orderElement.getWorkHours();
        }
        return result;
    }

    @Override
    public List<HoursGroup> getHoursGroups() {
        List<HoursGroup> hoursGroups = new ArrayList<HoursGroup>();
        for (OrderElement orderElement : children) {
            hoursGroups.addAll(orderElement.getHoursGroups());
        }
        return hoursGroups;
    }

    @Override
    public BigDecimal getAdvancePercentage() {
        Integer hours = getWorkHours();
        BigDecimal temp = new BigDecimal(0);

        if (hours > 0) {
            for (OrderElement orderElement : children) {
                BigDecimal childPercentage = orderElement
                        .getAdvancePercentage();
                Integer childHours = orderElement.getWorkHours();
                temp = temp.add(childPercentage.multiply(new BigDecimal(
                        childHours)));
            }

            temp = temp.divide(new BigDecimal(hours));
        }

        Set<AdvanceAssigment> advanceAssigments = getAdvanceAssigments();
        if (!advanceAssigments.isEmpty()) {
            for (AdvanceAssigment advanceAssigment : advanceAssigments) {
                BigDecimal percentage = advanceAssigment.getLastPercentage();
                temp = temp.add(percentage);
            }

            Integer number = advanceAssigments.size() + 1;
            temp = temp.divide(new BigDecimal(number));
        }

        return temp.setScale(2);
    }

}
