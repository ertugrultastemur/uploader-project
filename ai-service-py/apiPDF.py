# from flask import Flask, request
#
# app = Flask(__name__)
#
# @app.route('/pdfread', methods=['POST'])
# def upload():
#     data = request.data  # byte[] veri tipini al
#     print(data)          # konsola yazdır
#     return "Veri başarıyla alındı!", 200
#
# if __name__ == "__main__":
#     app.run(host='0.0.0.0', port=7878)
# ---------------------------------------------------------------------
# from flask import Flask, request
# import PyPDF2
# from io import BytesIO
#
# app = Flask(__name__)
#
# @app.route('/pdfread', methods=['POST'])
# def upload():
#     data = request.data  # byte[] veri tipini al
#
#     # Gelen byte verisini BytesIO nesnesine dönüştür
#     pdf_data = BytesIO(data)
#
#     # PDF okuyucusunu oluştur (PdfReader sınıfını kullanarak)
#     pdf_reader = PyPDF2.PdfReader(pdf_data)
#
#     # Tüm sayfaları oku ve metni konsola yazdır
#     text = "asd"
#     for page_num in range(len(pdf_reader.pages)):
#         page = pdf_reader.pages[page_num]
#         text += page.extract_text()
#
#     print(text)
#     return "Veri başarıyla alındı!", 200
#
# if __name__ == "__main__":
#     app.run(host='0.0.0.0', port=7878)

# ---------------------------------------------------------------------

# app.py

from flask import Flask, request, jsonify
import os

UPLOAD_FOLDER = 'uploads'
ALLOWED_EXTENSIONS = {'pdf'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/pdfread', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return jsonify({'error': 'No file part'}), 400

    file = request.files['file']

    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400

    if file and allowed_file(file.filename):
        filename = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
        file.save(filename)
        return jsonify({'message': 'File uploaded successfully'}), 200

    return jsonify({'error': 'Invalid file type'}), 400

if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    app.run(host='0.0.0.0', port=7878)
