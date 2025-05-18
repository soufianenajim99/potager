import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add request interceptor if needed
apiClient.interceptors.request.use(
    (config) => {
        // You can modify requests here (e.g., add auth token)
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        // Handle errors globally
        return Promise.reject(error);
    }
);

export default apiClient;