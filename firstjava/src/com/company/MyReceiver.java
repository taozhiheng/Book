package com.company;

/**
 * Created by taozhiheng on 14-11-4.
 */
public class MyReceiver {

    PersonActivity person;
    public MyReceiver(PersonActivity person)
    {
        this.person=person;
    }
    public void toGetUp()
    {
        person.getUp();
    }
    public void toHaveBreakfast()
    {
        person.haveBreakfast();
    }
    public void toHaveLunch()
    {
        person.haveLunch();
    }
    public void toHaveDinner()
    {
        person.haveDinner();
    }
    public void toGoToBed()
    {
        person.goToBed();
    }
}
