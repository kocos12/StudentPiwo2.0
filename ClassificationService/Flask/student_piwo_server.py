from flask import Flask, request, jsonify
import torch as tc
import torchvision as tv
from PIL import Image
import os
from pathlib import Path
import numpy as np
import tempfile


app = Flask(__name__)

device = "cuda:0" if tc.cuda.is_available() else "cpu"

model = tc.load('resnet18_whole_model.pth', map_location = device)
model.eval()

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({'error': 'No image uploaded'}), 400

    image = request.files['image']

    if image.filename == '':
        return jsonify({'error': 'No image selected for uploading'}), 400
    try:
        image = Image.open(image)
    except IOError:
        return jsonify({'error': 'Invalid image file'}), 400
    
    # Save the image to a temporary file
    with tempfile.NamedTemporaryFile(suffix=".jpg", delete=False) as temp_img:
        image.save(temp_img.name)
        image = tv.io.read_image(temp_img.name)

    image = image.numpy()
    image = image.astype(np.float32)
    image = tc.from_numpy(image)
      
    if (len(image.shape) == 2):
        image = image.unsqueeze(2)
    if image.shape[2] == 1:
        image = image.repeat(1, 1, 3)
    if image.shape[2] == 4:
        image = image[:, :, :3]

    image = tv.transforms.functional.resize(image, (224,224), antialias=True)
    image = tv.transforms.functional.normalize(image, (0.485, 0.456, 0.406), (0.229, 0.224, 0.225))
    image = image.unsqueeze(0)

    #print(image.shape)
    image = image.to(device)

    # Predict the image class
    with tc.no_grad():
        output = model(image)
        prediction = output.argmax(dim=1)
        prediction = prediction.cpu().item()
        print("Predicted class number is " + str(prediction))

    return jsonify({'prediction': prediction})

if __name__ == '__main__':
    app.run()