// import { render, screen, fireEvent, waitFor } from '@testing-library/react';
// import Home from '../src/app/page';
// import { useToast, ToasterToast } from '@/components/ui/use-toast';

// // Define the `Toast` type
// type Toast = {
//   title: string;
//   // Add other properties as needed
// };

// // Define the type for the mock toast function
// type ToastMock = {
//   toast: ({ ...props }: Toast) => { id: string; dismiss: () => void; update: (props: ToasterToast) => void; };
//   dismiss: (toastId?: string) => void;
//   toasts: ToasterToast[];
// };

// // Mock the useToast hook
// jest.mock('@/components/ui/use-toast', () => ({
//   useToast: jest.fn(),
// }));

// describe('Home Component', () => {
//   let toastMock: ToastMock;

//   beforeEach(() => {
//     toastMock = { toast: jest.fn() } as unknown as ToastMock;
//     (useToast as jest.Mock).mockReturnValue(toastMock);
//   });

//   test('renders the initial UI correctly', () => {
//     render(<Home />);
    
//     expect(screen.getByText('Bag-of-Words Generator')).toBeInTheDocument();
//     expect(screen.getByText('Upload your .txt file here. Not exceeding 200mb.')).toBeInTheDocument();
//   });

//   test('handles file upload correctly', async () => {
//     render(<Home />);

//     const fileInput = screen.getByLabelText('Text File') as HTMLInputElement;
//     const file = new File(['dummy content'], 'example.txt', { type: 'text/plain' });
    
//     fireEvent.change(fileInput, { target: { files: [file] } });
    
//     if (fileInput.files) {
//       expect(fileInput.files[0]).toEqual(file);
//       expect(fileInput.files).toHaveLength(1);
//     }
//   });

//   test('shows a toast message if no file is selected on upload', async () => {
//     render(<Home />);
    
//     const uploadButton = screen.getByText('Upload');
//     fireEvent.click(uploadButton);
    
//     await waitFor(() => {
//       expect(toastMock.toast).toHaveBeenCalledWith({ title: '💡 Please select a file' });
//     });
//   });

//   test('handles algorithm option change correctly', () => {
//     render(<Home />);
    
//     const sequentialRadio = screen.getByLabelText('Sequential') as HTMLInputElement;
//     const javaStreamRadio = screen.getByLabelText('Concurrent / JavaStream') as HTMLInputElement;
    
//     fireEvent.click(javaStreamRadio);
//     expect(javaStreamRadio).toBeChecked();
//     expect(sequentialRadio).not.toBeChecked();
//   });

//   test('handles sort switch change correctly', () => {
//     render(<Home />);
    
//     const sortSwitch = screen.getByLabelText('Sort Ascending') as HTMLInputElement;
//     fireEvent.click(sortSwitch);
//     expect(sortSwitch).toBeChecked();
//   });

//   test('handles slider change correctly', () => {
//     render(<Home />);
    
//     const slider = screen.getByLabelText('Number of Words: 50') as HTMLInputElement;
//     fireEvent.change(slider, { target: { value: 30 } });
    
//     expect(screen.getByLabelText('Number of Words: 30')).toBeInTheDocument();
//   });

//   test('handles form submission and displays results', async () => {
//     global.fetch = jest.fn(() =>
//       Promise.resolve({
//         ok: true,
//         json: () => Promise.resolve({ data: { word1: 1, word2: 2 }, elapsed_time: 123, file_processing_time: 45, algorithm_processing_time: 78 }),
//       })
//     ) as jest.Mock;

//     render(<Home />);
    
//     const fileInput = screen.getByLabelText('Text File') as HTMLInputElement;
//     const file = new File(['dummy content'], 'example.txt', { type: 'text/plain' });
//     fireEvent.change(fileInput, { target: { files: [file] } });
    
//     const uploadButton = screen.getByText('Upload');
//     fireEvent.click(uploadButton);
    
//     await waitFor(() => {
//       expect(toastMock.toast).toHaveBeenCalledWith({ title: '✅ File uploaded successfully' });
//       expect(screen.getByText('Elapsed Time: 123 ms')).toBeInTheDocument();
//       expect(screen.getByText('word1: 1')).toBeInTheDocument();
//       expect(screen.getByText('word2: 2')).toBeInTheDocument();
//     });
//   });
// });
