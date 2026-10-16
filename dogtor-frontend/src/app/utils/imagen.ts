/**
 * Lee una imagen elegida por la persona y la reduce para guardarla como data URL JPEG.
 * Evita subir fotos de varios MB a la base de datos.
 */
export function comprimirImagen(archivo: File, ladoMaximo = 600, calidad = 0.82): Promise<string> {
  return new Promise((resolve, reject) => {
    if (!archivo.type.startsWith('image/')) {
      reject(new Error('El archivo debe ser una imagen'));
      return;
    }
    const lector = new FileReader();
    lector.onerror = () => reject(new Error('No se pudo leer la imagen'));
    lector.onload = () => {
      const img = new Image();
      img.onerror = () => reject(new Error('No se pudo leer la imagen'));
      img.onload = () => {
        const escala = Math.min(1, ladoMaximo / Math.max(img.width, img.height));
        const canvas = document.createElement('canvas');
        canvas.width = Math.round(img.width * escala);
        canvas.height = Math.round(img.height * escala);
        canvas.getContext('2d')!.drawImage(img, 0, 0, canvas.width, canvas.height);
        resolve(canvas.toDataURL('image/jpeg', calidad));
      };
      img.src = lector.result as string;
    };
    lector.readAsDataURL(archivo);
  });
}
