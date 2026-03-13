/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagString
 *  net.minecraft.util.EnumChatFormatting
 */
package net.nmccoy.legendgear;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

public class Fortunes {
    public List<String> fortunes = new ArrayList<String>();
    public List<String> ritualHints = new ArrayList<String>();
    public Random rand = new Random();

    public Fortunes() {
        this.storeFortunes();
    }

    public String randomFortune() {
        return this.fortunes.get(this.rand.nextInt(this.fortunes.size()));
    }

    public String randomRitualHint() {
        return this.ritualHints.get(this.rand.nextInt(this.ritualHints.size()));
    }

    public ItemStack makeRitualNotebook() {
        ItemStack book = new ItemStack(Items.written_book);
        if (!book.hasTagCompound()) {
            book.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = book.getTagCompound();
        tag.setString("title", "Ritual Primer");
        tag.setString("author", "Nalathni the Abstruse");
        NBTTagList pages = new NBTTagList();
        for (String hint : this.ritualHints) {
            pages.appendTag((NBTBase)new NBTTagString(hint));
        }
        tag.setTag("pages", (NBTBase)pages);
        return book;
    }

    public void storeFortunes() {
        this.fortunes.add("Don't believe everything you eat.");
        this.fortunes.add("They say the best defense is a good oak fence.");
        this.fortunes.add("Emeralds don't grow on trees.");
        this.fortunes.add("Health begets wealth.");
        this.fortunes.add("Herobrine isn't real.");
        this.fortunes.add("This cookie intentionally left blank.");
        this.fortunes.add("They say the Phoenix likes sunflowers.");
        this.fortunes.add("Things that like fire don't like ice.");
        this.fortunes.add("Wet things are more conductive.");
        this.fortunes.add("Don't get sloppy when working with teleportation.");
        this.fortunes.add("The full moon is magical.");
        this.fortunes.add("Don't waste moonlight by sleeping.");
        this.fortunes.add("Nudity is magical.");
        this.fortunes.add("Magic begins and ends with the stars.");
        this.fortunes.add("Don't bother with astrology.");
        this.fortunes.add("Look up to see something blue.");
        this.fortunes.add("The thing that's about to happen is just a coincidence.");
        this.fortunes.add("Fortune cookies are magical.");
        this.fortunes.add("Stardust is magical.");
        this.fortunes.add("You are magical.");
        this.fortunes.add("Redstone connects things to other things.");
        this.fortunes.add("Gold is a very pure element.");
        this.fortunes.add("Sometimes these things are useless.");
        this.fortunes.add("Friendship is magical.");
        this.fortunes.add("The stars shine on everyone equally.");
        this.fortunes.add("Lawn mowing is no way to make a living.");
        this.fortunes.add("Retracing your steps is like not going anywhere.");
        this.fortunes.add("Take notes, it helps.");
        this.fortunes.add("A tuning fork can help when it's too bright to see.");
        this.fortunes.add("If you make a mistake, try again from the beginning.");
        this.fortunes.add("Remember to clean up after yourself.");
        this.fortunes.add("Don't take feathers for granted.");
        this.fortunes.add("Don't forget to sleep in real life.");
        this.fortunes.add("Insomnia is magical.");
        this.fortunes.add("You can read a cookie but you can't eat a book.");
        this.fortunes.add("Eat more fortune cookies.");
        this.fortunes.add("Orientation doesn't matter.");
        this.fortunes.add("I ship Jean/Steve.");
        this.fortunes.add("Copy this fortune cookie into your signature.");
        this.fortunes.add("I see a cryptic foreshadowing in your future.");
        this.fortunes.add("A shovel helps for digging up fragile things.");
        this.fortunes.add("You can't see the stars if it's not dark enough.");
        this.fortunes.add("Try using pistons.");
        this.fortunes.add("The kobolds love you.");
        this.fortunes.add("Lapis lazuli is associated with enchantment.");
        this.fortunes.add("A boomerang returns more than itself.");
        this.fortunes.add("The Phoenix despises undead.");
        this.fortunes.add("All rituals need a focus, even if it's you.");
        this.fortunes.add("Diamonds signify perfection.");
        this.fortunes.add("Starstone represents the astral realm.");
        this.fortunes.add("Emeralds are related to living beings.");
        this.fortunes.add("It is said that the stars favor brave adventurers.");
        this.fortunes.add("Offerings are rewarded at the whim of the spirits.");
        this.fortunes.add("The Phoenix watches over all the denizens of the World.");
        this.fortunes.add("Casting a spell perfectly gives a little more oomph.");
        this.fortunes.add("Many spells are dangerous to use underwater.");
        this.fortunes.add("The spirits lose interest if offerings are too frequent.");
        this.fortunes.add("Spoiler alert.");
        this.fortunes.add("The sacred symbols of the Phoenix are fire and gold.");
        this.fortunes.add("Staves can leak magic if you hit something hard enough.");
        this.fortunes.add("Spirits see mundane items as a request for a similar gift.");
        this.fortunes.add("There's a way to extract azurite without breaking it.");
        this.fortunes.add("Any spirit appreciates a gift of starstone.");
        this.fortunes.add("A phoenix quill is needed for magical writings.");
        this.fortunes.add("Starstone can power up a sky lens.");
        this.fortunes.add("Summoning signifiers always include part of a being's home.");
        this.fortunes.add("A summoning focus usually appeals to the target's interest.");
        this.fortunes.add("An altar can make offerings both easier and more effective.");
        this.ritualHints.add("PREPARATION\n\nBuild a ritual locus out of starglass: five lumps in an X shape, with gold nuggets in the interstices. Place it into an active starwell. You will see eight faintly glowing nodes forming a ritual circle.");
        this.ritualHints.add("THE BASICS\n\nWhen a block is placed on a node, it will form a link to another node. These links are visible under moonlight. Each starwell has its own pattern of links, formed in a set order. Tap the locus with a starsteel tuning fork to hear the resonance of the next link.");
        this.ritualHints.add("TERMINOLOGY\n\nA lattice is a set of two types of blocks linked in alternation, one type to the other. Many rituals also require a keystone, a block linked to no others. All rituals need a focus: an object or being placed at the center.");
        this.ritualHints.add("SUMMON HORSE\n\nSet a keystone of emerald, and construct a lattice of hay bales and grassy earth. Place a lump of sugar at the focus and invoke with infused stardust.");
        this.ritualHints.add("SOUL TETHER\n\nA dangerous ritual for " + EnumChatFormatting.GREEN + "experienced" + EnumChatFormatting.BLACK + " practitioners only! " + "Form a lattice of soul sand and string, with iron as your keystone. " + "The item you invoke this ritual upon will cling to your soul once upon death.");
        this.ritualHints.add("SPIRIT OFFERING\n\nTo contact an astral spirit, one requires a block of starstone as a key. To this, add a lattice of the spirit's signifiers and place your offering at the center. The standard sprinkling of infused stardust will suffice for most offerings.");
        this.ritualHints.add("ADDITIONAL NOTES\n\nThe rituals in this book are far from a complete list! Random experimentation would prove fruitless and costly; better to seek guidance and collaborate with your peers. (Consult your local fortune cookie for more dubious information.)");
    }
}

