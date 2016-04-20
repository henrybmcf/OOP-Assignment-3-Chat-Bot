# OOP-Assignment-3-Chat-Bot
#### Team Members: Henry Ballinger McFarlane (ChatBot code) & Rachel Wan (Visual code)

### Notes:
- Bundling into a jar file gave a whole bucket load of null pointer excepetions for some reason. So, the project will have to be run through IntelliJ, this will require importing the necessary libraries before it can run. These are all stored in the lib folder. In IntelliJ (on Mac), it can be done through 'File' -> 'Project Structure' -> 'Libraries'.
- The bot does talk, so please turn up your volume.

### Bot Execution

1. The first thing the bot will ask is what your name is. Here you must put in your full name, this is used to create your profile file (In this, the bot will remember any favourites that you tell it about)
2. After this, you are free to talk to the bot.
3. To exit the program, please use one of the following phrases: "bye", "goodbye", "see you later", "i have to go", "see ya".

### Orginal Basic Step-by-Step Plan
#### Increasing complexity of chat bot conversational abilities per step

1. Simplest implementation of a chat bot, to simply have a list of predefined responses. Select a response randomly from this list. This gives no conversation between the user and bot, simply a random response.
2. Moving on from this, will be the ability to recognise key words and phrases in the user's input. There will then be a database of responses, within which each keyword or phrase has several possible responses. One of these is selected randomly. This gives the user a sense that the bot has responded to their question. Additional to this would be to implement that multiple keywords can have the same set of responses, this will allow the user to ask a question in different ways and it still be recognised by the bot.
3. The keyword search can bring problems is their is punctuation or white space in the user's input, so the next step is to remove all of these unwanted characters before searching the database for a match to the keyword. Also implemented here will be the ability to save the bot's previously selected response, so that in the event that the user repeats themselves, the bot's response will be different from the one immediately prior.
4. The previous keyword search implementation only allowed for exact matches of a key phrase. However, we can improve the detection and matching ability by using Levenshtein's distance algorithm to determine if the user's input matching any keyword in the database closely enough to be classed as the same.
5. To improve the conversational abilities, we need the bot to think more about what the user is asking, for example if the there are two keyword matches for the input, the bot needs to determine the ranking or priority of the keywords, listing them in order of importance and then searching for a response to the top keyword.
6. Next would be to introduce improved replies, keyword transposition. This meaning that if the user asks the bot a question, for example, "You are not clever enough", the bot could turn this around by recognising the use of 'you are', knowing to replace it with "I am", meaning that the bot could reply with something like "So, you think I'm not clever enough?". This could be done by having some skeleton responses, basic templates that the bot can manipulate and use to improve it's responses. 
7. Introducing context to the bot's responses, that is if the user replies to an answer from the bot, then the bot knowing what the previous response was and replying to the second question from the user in context to the answer. For example, User: "What is your favourite food?". Bot: "It is chocolate.". User: "Why do you like it?". Bot: "I like it because it is tastes damn good.". The idea of this is give more of a flow and continuation to the conversation.
8. Saving files and coder controlled learning. If we save a log of all conversations held by the bot, then we can review it at points to see where the bot falls down and where it exceeds, applying this knowledge to improve the AI capabilities of the bot. This will also allow us to see the overall progress of the bot over time.
9. User controlled/Self-learning. If the bot does not know or recognise a keyword inputted by a user, then the bot can ask the user what it is, what it means and what a possible correct response could be. This will allow the bot to continuously learn and adapt it's knowledge database, thus improving it's conversational abilities.
10. The final stages can be to better the aesthetics of the bot. Text-to-speech could be implemented so that the bot can speak the answers back to the user. A simple sound/tone visualiser could be used to represent the voice and speech patterns. A possible further step would be to allow the user to speak to the bot, with a speech-to-text program, however this can be decided later, as implementation may be difficult.

All of theses steps are simple ideas at the minute, so the intermediate and final versions may and probably will vary from these points, with additions and deletions from this list.
