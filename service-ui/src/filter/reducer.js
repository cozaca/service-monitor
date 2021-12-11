export const filter = (state = '', action) => {
    switch (action.type) {
      case 'FILTER_CHANGED': {
        return action.filter
      }
      default:
        return state;
    }
  };
  
  export const count = (state = 0, action) => {
    switch (action.type) {
      case 'SERVICES_LOADED': {
        return action.services.length
      }
      case 'SERVICES_ADDED': {
        return state + 1;
      }
      case 'SERVICES_REMOVED': {
        return state - 1;
      }
      default:
        return state;
    }
  };