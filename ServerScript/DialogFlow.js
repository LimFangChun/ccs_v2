const dialogflow = require('dialogflow');
const uuid = require('uuid');
//set GOOGLE_APPLICATION_CREDENTIALS=C:\xampp\htdocs
/**
 * Send a query to the dialogflow agent, and return the query result.
 */
async function DialogFlow_Respond(inputText) {
  console.log("Inside runSample");
  // A unique identifier for the given session
  const sessionId = uuid.v4();

  // Create a new session
  const sessionClient = new dialogflow.SessionsClient();
  const sessionPath = sessionClient.sessionPath('communechatsystem', sessionId);

  // The text query request.
  const request = {
    session: sessionPath,
    queryInput: {
      text: {
        // The query to send to the dialogflow agent
        text: inputText,
        // The language used by the client (en-US)
        languageCode: 'en-US',
      },
    },
  };

  // Send request and log result
  const responses = await sessionClient.detectIntent(request);
  console.log(`============================`);
  console.log('Detected intent');
  const result = responses[0].queryResult;
  console.log(`Query: ${result.queryText}`);
  console.log(`Response:\n\n${result.fulfillmentMessages[0].text.text[0]}\n\n`);
  console.log(`============================`);
  if (result.intent) {
    console.log(`  Intent: ${result.intent.displayName}`);
  } else {
    console.log(`  No intent matched.`);
  }

  return result.fulfillmentMessages[0].text.text[0];
}

module.exports = {
    DialogFlow_Respond
}