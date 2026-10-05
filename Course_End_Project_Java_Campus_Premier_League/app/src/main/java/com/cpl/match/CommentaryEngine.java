package com.cpl.match;

import java.util.Random;

/**
 * Commentary generation engine synthesizing real-time cricket text descriptions.
 * Demonstrates:
 * - Unit II: String and StringBuffer
 */
public class CommentaryEngine {
    private final Random random = new Random();

    private final String[] dotTemplates = {
        "{bowler} fires in a dot ball, {batter} defends solidly towards mid-off.",
        "Good length outside off, {batter} shoulders arms. Dot ball.",
        "Beaten for pace! {bowler} beats the outside edge of {batter}'s bat.",
        "Pushed firmly towards cover, but no run available."
    };

    private final String[] singleTemplates = {
        "{batter} steers it behind square for a comfortable single.",
        "Tucked off the hips towards deep midwicket, {batter} rotates the strike.",
        "Dabbed softly to third man for an easy single.",
        "Driven down to long-on, {batter} jogs across to the danger end."
    };

    private final String[] twoTemplates = {
        "Clipped through midwicket, great running between the wickets gets two!",
        "Driven in the gap between cover and point, they hustle back for the second.",
        "Whipped away off the pads to fine leg, comfortable brace taken."
    };

    private final String[] fourTemplates = {
        "FOUR! Cracking cover drive! {batter} leans into the pitch and finds the fence.",
        "FOUR! Short and punished! {batter} pulls authoritatively through square leg.",
        "FOUR! Edge and through the slips! Races away to the third man boundary!",
        "FOUR! Pierces the gap between point and cover with surgical precision!"
    };

    private final String[] sixTemplates = {
        "SIX! MASSIVE! {batter} launches {bowler} high into the campus grandstands!",
        "SIX! Picked up off the pads and dispatched over deep square leg! Pure timing!",
        "SIX! Dancing down the track, {batter} lofts it straight over long-on for a monster maximum!",
        "SIX! Upper cut over third man! Clears the rope with plenty to spare!"
    };

    private final String[] wicketTemplates = {
        "OUT! BOWLED HIM! {bowler} knocks back the off-stump! {batter} departs!",
        "OUT! CAUGHT! Skyed high in the air, the fielder settles underneath and takes a calm catch!",
        "OUT! LBW! Trapped right in front of middle! Plumb as a ruler, umpire raises the finger!",
        "OUT! GLOVED BEHIND! Thin edge through to the wicket-keeper, huge breakthrough for {bowler}!"
    };

    public String generateCommentary(String bowler, String batter, int runs, boolean isWicket, String extraType) {
        // Unit II: Use StringBuffer to assemble commentary text
        StringBuffer sb = new StringBuffer();

        if ("WIDE".equalsIgnoreCase(extraType)) {
            sb.append("WIDE! ").append(bowler).append(" sprays it down the leg side, penalized an extra run.");
            return sb.toString();
        }
        if ("NO_BALL".equalsIgnoreCase(extraType)) {
            sb.append("NO BALL! ").append(bowler).append(" oversteps the crease! Free hit coming up!");
            return sb.toString();
        }

        if (isWicket) {
            String tmpl = wicketTemplates[random.nextInt(wicketTemplates.length)];
            sb.append(tmpl.replace("{bowler}", bowler).replace("{batter}", batter));
            return sb.toString();
        }

        String tmpl;
        switch (runs) {
            case 0:
                tmpl = dotTemplates[random.nextInt(dotTemplates.length)];
                break;
            case 1:
                tmpl = singleTemplates[random.nextInt(singleTemplates.length)];
                break;
            case 2:
            case 3:
                tmpl = twoTemplates[random.nextInt(twoTemplates.length)];
                break;
            case 4:
                tmpl = fourTemplates[random.nextInt(fourTemplates.length)];
                break;
            case 6:
                tmpl = sixTemplates[random.nextInt(sixTemplates.length)];
                break;
            default:
                tmpl = "{batter} takes " + runs + " runs off {bowler}.";
        }

        sb.append(tmpl.replace("{bowler}", bowler).replace("{batter}", batter));
        return sb.toString();
    }
}
