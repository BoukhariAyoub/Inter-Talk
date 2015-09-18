package com.aboukhari.intertalking.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.aboukhari.intertalking.database.DatabaseManager;
import com.aboukhari.intertalking.model.TranslatedMessage;
import com.aboukhari.intertalking.retrofit.RestClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by aboukhari on 02/09/2015.
 */


public class TranslationManager {

    public static void translateMessage(final Context context, String lang, final String messageId, final String text, final TextView messageTextView, boolean auto_translate) {
        messageTextView.setText("Translating...");
        final DatabaseManager databaseManager = DatabaseManager.getInstance(context);
        TranslatedMessage message = databaseManager.getMessage(messageId);
        //TODO check if lang == selected lang too
        if (message != null) {
            //TODO add boolean and do this
            messageTextView.setText(message.getText());
            Log.d("natija trans", "from database : " + messageId + " : " + message.getText());
        } else if (auto_translate) {
            RestClient.get(Constants.YANDEX_TRANSLATE_API_ENDPOINT).getTranslation(Constants.YANDEX_API_KEY, lang, text, "1", new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    int code = jsonObject.get("code").getAsInt();
                    if (code == 200) {
                        String lang = jsonObject.get("lang").getAsString();
                        String translatedText = jsonObject.get("text").getAsJsonArray().get(0).getAsString();
                        messageTextView.setText(translatedText);
                        TranslatedMessage translatedMessage = new TranslatedMessage(messageId, translatedText, lang);
                        databaseManager.addMessage(translatedMessage);
                        Log.d("natija trans", "from yandex");
                        Log.d("natija trans", "from yandexe : " + messageId + " : " + translatedMessage.getText());


                    } else {
                        Log.d("natija trans", "there was an erroe code : " + code);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("natija trans", "there was an error : " + error.getMessage());
                }
            });

        }
        else{
            messageTextView.setText(text);
        }


    }
}
