# OOP-Assignment-3-Chat-Bot

### Basic Step-by-Step Guide

####Increasing complexity of chat bot conversational abilities per step

1. Simplest implenetation of a chat bot, to simply have a list of predefined responses. Select a response randomly from this list. This gives no conversation between the user and bot, simply a random response.
2. Moving on from this, will be the ability to recognise key words and phrases in the user's input. There will then be a databse of responses, within which each keyword or phrase has several possible responses. One of these is selected randomly. This gives the user a sense that the bot has responsed to their question.
3. The keyword search can bring problems is their is punctutation or white space in the user's input, so the next step is to remove all of these unwanted characters before searching the database for a match to the keyword. Also implemented here will be the ability to save the bot's previously selected response, so that in the event that the user repeats themselves, the bot's response will be different from the one immediately prior.
4. The previous keyword search implementation only allowed for exact matches of a key phrase. However, we can improve the detection and matching ability by using Levenshtein's distance alogrithm to determine if the user's input matching any keyword in the database closely enough to be classed as the same.
5. 