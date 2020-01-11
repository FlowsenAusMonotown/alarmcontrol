import firebaseApp from './firebaseApp';
import firebase from "firebase";

export const startRegisterServiceWorker = (config) => {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./serviceWorker.js?config=' + encodeURIComponent(JSON.stringify(config)))
    .then(function(registration) {
      console.log('ServiceWorker Registration successful, scope is:', registration.scope);

      if(firebase.messaging.isSupported()){
        firebaseApp.messaging().useServiceWorker(registration);
      }else{
        console.log("Firebase Messaging is not supported in this Browser!");
      }
    })
    .catch(function(error) {
      console.log('Service worker registration failed, error:', error);
    });
  }else {
    console.log("ServiceWorker are not supported");
  }
};
