# Use a slim base image
FROM node:alpine

# Set the working directory
WORKDIR /usr/src/app

# Copy package.json and package-lock.json to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code
COPY . .

# Expose port 4200 (assuming you're serving the Angular app on port 4200)
EXPOSE 4200

# Command to start the Angular development server
CMD ["npm", "start"]
