export const logSuccess = (message) => {
  console.log('%c✅ ' + message, 'color: green; font-weight: bold;');
};

export const logError = (error) => {
  console.log('%c❌ Erro: ' + (error.response?.data?.message || error.message), 'color: red; font-weight: bold;');
};

export const logInfo = (message) => {
  console.log('%cℹ️ ' + message, 'color: blue; font-weight: bold;');
}; 