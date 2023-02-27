// BirthdayGreeter.java
// D. Singletary
// 2/15/23
// An interface for building and sending birthday cards.

package edu.fscj.cop2805c.birthday;

public interface BirthdayGreeter {
    // build a birthday card for a with a greeting
    public BirthdayCard buildCard(User u, String msg);
    // send a birthday card
    public void sendCard(BirthdayCard bc);
}
