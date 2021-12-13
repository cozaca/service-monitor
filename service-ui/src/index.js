import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import { Provider } from 'react-redux';
import { services } from './services/reducer';
import { filter, count } from './filter/reducer';

const rootReducer = combineReducers({
  services,
  count,
  filter
});

// used for async actions
const servicesMdl = store => next => action => {
  switch(action.type) {
    case 'SERVICES_LOAD': {
      fetch("http://localhost:8081/api/services")
        .then(res => res.json())
        .then(services => {
          store.dispatch({ type: 'SERVICES_LOADED', services });
        });
      break;
    }
    case 'SERVICES_ADD': {
      const service = action.service;
      console.log(JSON.stringify(service));
      fetch('http://localhost:8081/api/services', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(service),
      })
        .then(res => res.json())
        .then(r => {
          console.log("data is " + r);
          if (r.success) {
            service.id = r.id;
            store.dispatch({ type: 'SERVICES_ADDED', service });
          }
        })
        .catch((err) => {
          console.error("Error : " + err);
        });
      break;
    }
    case 'SERVICES_REMOVE': {
      fetch("http://localhost:8081/api/services/" + action.id, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ id: action.id })
      }).then(r => r.json()).then(status => {
        store.dispatch({ type: 'SERVICES_REMOVED', id: action.id })
      });
      break;
    }
    default: 
      break;
  }
  return next(action);
};

const store = createStore(rootReducer, applyMiddleware(servicesMdl));

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();