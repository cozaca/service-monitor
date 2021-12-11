export const services = (state = [], action) => {
    switch (action.type) {
      case 'SERVICES_LOADED': {
        return action.services
      }
      case 'SERVICES_ADDED': {
        return [...state, action.service];
      }
      case 'SERVICES_REMOVED': {
        return state.filter(service => service.id !== action.id);
      }
      default:
        return state;
    }
  };