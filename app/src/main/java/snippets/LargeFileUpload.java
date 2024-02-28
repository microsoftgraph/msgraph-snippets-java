// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.io.*;
import java.util.concurrent.CancellationException;

import com.microsoft.graph.core.exceptions.ClientException;
import com.microsoft.graph.core.models.IProgressCallback;
import com.microsoft.graph.core.models.UploadResult;
import com.microsoft.graph.core.tasks.LargeFileUploadTask;
import com.microsoft.graph.drives.item.items.item.createuploadsession.CreateUploadSessionPostRequestBody;
import com.microsoft.graph.models.AttachmentItem;
import com.microsoft.graph.models.AttachmentType;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.serviceclient.GraphServiceClient;

public class LargeFileUpload {
    public static void runSamples(GraphServiceClient graphClient, String filePath)
        throws Exception {
        final String itemPath = "Documents/vacation.gif";

        uploadFileToOneDrive(graphClient, filePath, itemPath);
        uploadAttachmentToMessage(graphClient, filePath);
    }

    private static void uploadFileToOneDrive(GraphServiceClient graphClient,
        String filePath, String itemPath) throws Exception {
        if (null == filePath || null == itemPath) {
            throw new Exception("Parameters are not optional");
        }
        // <LargeFileUploadSnippet>
        // Get an input stream for the file
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);
        long streamSize = file.length();

        // Set body of the upload session request
        CreateUploadSessionPostRequestBody uploadSessionRequest = new CreateUploadSessionPostRequestBody();
        DriveItemUploadableProperties properties = new DriveItemUploadableProperties();
        properties.getAdditionalData().put("@microsoft.graph.conflictBehavior", "replace");
        uploadSessionRequest.setItem(properties);

        // Create an upload session
        // ItemPath does not need to be a path to an existing item
        String myDriveId = graphClient.me().drive().get().getId();
        UploadSession uploadSession = graphClient.drives()
                .byDriveId(myDriveId)
                .items()
                .byDriveItemId("root:/"+itemPath+":")
                .createUploadSession()
                .post(uploadSessionRequest);

        // Create the upload task
        int maxSliceSize = 320 * 10;
        LargeFileUploadTask<DriveItem> largeFileUploadTask = new LargeFileUploadTask<>(
                graphClient.getRequestAdapter(),
                uploadSession,
                fileStream,
                streamSize,
                maxSliceSize,
                DriveItem::createFromDiscriminatorValue);

        int maxAttempts = 5;
        // Create a callback used by the upload provider
        IProgressCallback callback = (current, max) -> System.out.println(
                    String.format("Uploaded %d bytes of %d total bytes", current, max));

        // Do the upload
        try {
            UploadResult<DriveItem> uploadResult = largeFileUploadTask.upload(maxAttempts, callback);
            if (uploadResult.isUploadSuccessful()) {
                System.out.println("Upload complete");
                System.out.println("Item ID: " + uploadResult.itemResponse.getId());
            } else {
                System.out.println("Upload failed");
            }
        } catch (CancellationException ex) {
            System.out.println("Error uploading: " + ex.getMessage());
        }
        // </LargeFileUploadSnippet>
    }

    public static void resumeableUpload(LargeFileUploadTask<DriveItem> largeFileUploadTask, IProgressCallback callback)
            throws ClientException, IOException, InterruptedException {
        // <ResumeSnippet>
        int maxAttempts = 5;
        largeFileUploadTask.resume(maxAttempts, callback);
        // </ResumeSnippet>
    }

    private static void uploadAttachmentToMessage(GraphServiceClient graphClient,
        String filePath) throws Exception {
        if (null == filePath) {
            throw new Exception("Parameters are not optional.");
        }
        // <UploadAttachmentSnippet>
        // Create message
        Message draftMessage = new Message();
        draftMessage.setSubject("Large attachment");
        Message savedDraft = graphClient.me().messages().post(draftMessage);

        // Get an input stream for the file
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);
        long streamSize = file.length();

        final AttachmentItem largeAttachment = new AttachmentItem();
        largeAttachment.setAttachmentType(AttachmentType.File);
        largeAttachment.setName(file.getName());
        largeAttachment.setSize(streamSize);

        com.microsoft.graph.users.item.messages.item.attachments.createuploadsession.CreateUploadSessionPostRequestBody uploadRequestBody
                = new com.microsoft.graph.users.item.messages.item.attachments.createuploadsession.CreateUploadSessionPostRequestBody();
        uploadRequestBody.setAttachmentItem(largeAttachment);

        final UploadSession uploadSession = graphClient.me()
                .messages()
                .byMessageId(savedDraft.getId())
                .attachments()
                .createUploadSession()
                .post(uploadRequestBody);

        LargeFileUploadTask<FileAttachment> largeFileUploadTask = new LargeFileUploadTask<>(
                graphClient.getRequestAdapter(),
                uploadSession,
                fileStream,
                streamSize,
                FileAttachment::createFromDiscriminatorValue);

        int maxAttempts = 5;
        // Create a callback used by the upload provider
        IProgressCallback callback = (current, max) -> System.out.println(
                String.format("Uploaded %d bytes of %d total bytes", current, max));

        // Do the upload
        try {
            UploadResult<FileAttachment> uploadResult = largeFileUploadTask.upload(maxAttempts, callback);
            if (uploadResult.isUploadSuccessful()) {
                System.out.println("Upload complete");
                System.out.println("Item ID: " + uploadResult.itemResponse.getId());
            } else {
                System.out.println("Upload failed");
            }
        } catch (CancellationException ex) {
            System.out.println("Error uploading: " + ex.getMessage());
        }
        // </UploadAttachmentSnippet>
    }
}
