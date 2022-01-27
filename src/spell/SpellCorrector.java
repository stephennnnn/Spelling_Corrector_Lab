package spell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class SpellCorrector implements ISpellCorrector {
    /*
    Main will first call useDictionary, which reads in an input file and creates a dictionary from all the words.

    Main will then call suggestSimilarWord, which will read a misspelled string and suggest a correct word with the 4 edit distances.
     */

    Trie trie;  // Dictionary

    public SpellCorrector() {
        trie = new Trie();
    }

    public Trie getTrie() {
        return trie;
    }

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {
        // Reads in an input file and fleshes out a dictionary from all the words.

        File dictionaryFile = new File(dictionaryFileName);

        Scanner input = new Scanner(dictionaryFile);
        while (input.hasNext()) {
            String newWord = input.next();
            trie.add(newWord);
        }
        input.close();
    }

    @Override
    public String suggestSimilarWord(String inputWord) {
        inputWord = inputWord.toLowerCase();        // ...Could also use trim() and checking to make sure it is not null.
        Node findNode = trie.find(inputWord);

        if (findNode != null) {                     // If word is in dictionary, return word.
            return inputWord;
        }
        else {
//            If there is no word in the dictionary that has an edit distance of 1 or 2 then there is no word in the dictionary “similar” to the input string. In that case return null.
//
//            Defining the MOST similar word:
//            1. It has the “closest” edit distance from the input string
//            2. If multiple words have the same edit distance, the most similar word is the one with the closest edit distance that is found the most times in the dictionary
//            3. If multiple words are the same edit distance and have the same count/frequency, the most similar word is the one that is first alphabetically


            HashMap<Integer, ArrayList<String>> similarWords = new HashMap<>();
            ArrayList<String> changedWords = new ArrayList<>();


            // Find edit distance 1 words
            changedWords.addAll(change(inputWord));     // change
            changedWords.addAll(omit(inputWord));       // omit
            changedWords.addAll(insert(inputWord));     // insert
            changedWords.addAll(transpose(inputWord));  // transpose
            // Go through all the changed words: if in dictionary, add word + value to hashmap
            for (int i = 0; i < changedWords.size(); i++) {
                Node foundNode = trie.find(changedWords.get(i));
                if (foundNode != null) {
                    if (similarWords.containsKey(foundNode.getValue())) {   // if there are already words with the same frequency in the hasmap
                        similarWords.get(foundNode.getValue()).add(changedWords.get((i)));
                    }
                    else {                                                  // if you haven't added any words with this frequency yet, create new pair in hashmap and add to ArrayList
                        similarWords.put(foundNode.getValue(), new ArrayList<>());
                        similarWords.get(foundNode.getValue()).add(changedWords.get((i)));
                    }
                }
            }


            // If no similar words, find edit distance 2 words (can reuse similarWords since it is empty)
            if (similarWords.isEmpty()) {
                // throw all changedWords through the editor again to get changedTwiceWords
                ArrayList<String> changedTwiceWords = new ArrayList<>();
                for (int i = 0; i < changedWords.size(); i++) {
                    changedTwiceWords.addAll(change(changedWords.get(i)));
                    changedTwiceWords.addAll(omit(changedWords.get(i)));
                    changedTwiceWords.addAll(insert(changedWords.get(i)));
                    changedTwiceWords.addAll(transpose(changedWords.get(i)));
                }
                // Go through all the changed twice words: if in dictionary, add word + value to hashmap
                for (int i = 0; i < changedTwiceWords.size(); i++) {
                    Node foundNode = trie.find(changedTwiceWords.get(i));
                    if (foundNode != null) {
                        if (similarWords.containsKey(foundNode.getValue())) {
                            similarWords.get(foundNode.getValue()).add(changedTwiceWords.get((i)));
                        }
                        else {
                            similarWords.put(foundNode.getValue(), new ArrayList<String>());
                            similarWords.get(foundNode.getValue()).add(changedTwiceWords.get((i)));
                        }
                    }
                }
            }


            // If still no similar words, return null
            if (similarWords.isEmpty()) {
                return null;
            }
            else {  // return most similar word
                // sort value: find highest value out of similarWords, then add all words with highest value to arraylist (prioritizedWords)
                // find highest value key
                int highestKey = similarWords.keySet().iterator().next(); // initialize to first item of set
                for (int value : similarWords.keySet()) {
                    if (value > highestKey) {
                        highestKey = value;
                    }
                }
                // add all words with highest value key to prioritizedWords
                ArrayList<String> prioiritizedWords = new ArrayList<>(similarWords.get(highestKey));

                // sort alphabetically: add
                Collections.sort(prioiritizedWords);
                return prioiritizedWords.get(0);
            }
        }
    }

    // ---------------------------------- suggestSimilarWord helpers ----------------------------------

    // Change - Use a different character (generates 25n words)
    private ArrayList<String> change(String inputWord) {
        ArrayList<String> similarArray = new ArrayList<String>();
        String similarWord;

        for (int i = 0; i < inputWord.length(); i++) {
            for (int j = 0; j < 26; j++) {
                char changeChar = (char)('a' + j);
                char currentChar = inputWord.charAt(i);
                if (changeChar != currentChar) {
                    similarWord = inputWord.substring(0, i) + changeChar + inputWord.substring(i + 1);    // substring: start index is inclusive, end index is exclusive
                    similarArray.add(similarWord);
                }
            }
        }

        return similarArray;
    }

    // Omit - Omit a character (generates n words)
    private ArrayList<String> omit(String inputWord) {
        ArrayList<String> similarArray = new ArrayList<String>();
        String similarWord;

        for (int i = 0; i < inputWord.length(); i++) {
            similarWord = inputWord.substring(0, i) + inputWord.substring(i + 1);
            similarArray.add(similarWord);
        }

        return similarArray;
    }

    // Insert - Insert an extra character (generates 26(n+1) words)
    private ArrayList<String> insert(String inputWord) {
        ArrayList<String> similarArray = new ArrayList<String>();
        String similarWord;

        for (int i = 0; i < (inputWord.length() + 1); i++) {    // For every character in the word
            for (int j = 0; j < 26; j++) {                      // and for every letter in the alphabet
                char insertChar = (char)('a' + j);
                similarWord = inputWord.substring(0, i) + insertChar + inputWord.substring(i);
                similarArray.add(similarWord);
            }
        }

        return similarArray;
    }

    // Transpose - Transpose two adjacent characters (generates n-1 words)
    private ArrayList<String> transpose(String inputWord) {
        ArrayList<String> similarArray = new ArrayList<String>();
        String similarWord;

        for (int i = 0; i < (inputWord.length() - 1); i++) {
            similarWord = inputWord.substring(0, i) + inputWord.charAt(i + 1) + inputWord.charAt(i) + inputWord.substring(i + 2);
            similarArray.add(similarWord);
        }

        return similarArray;
    }

}
