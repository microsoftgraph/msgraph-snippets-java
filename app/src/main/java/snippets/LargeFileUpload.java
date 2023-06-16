// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.microsoft.graph.models.AttachmentCreateUploadSessionParameterSet;
import com.microsoft.graph.models.AttachmentItem;
import com.microsoft.graph.models.AttachmentType;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCreateUploadSessionParameterSet;
import com.microsoft.graph.models.DriveItemUploadableProperties;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.tasks.IProgressCallback;
import com.microsoft.graph.tasks.LargeFileUploadTask;

import okhttp3.Request;

public class LargeFileUpload {
    public static void runSamples(GraphServiceClient<Request> graphClient)
        throws Exception {
        final String filePath = "C:/Users/jasonjoh/OneDrive - Microsoft/Pictures/vacation.gif";
        final String itemPath = "Documents/vacation.gif";

        uploadFileToOneDrive(graphClient, filePath, itemPath);
        uploadAttachmentToMessage(graphClient, filePath);
    }

    private static void uploadFileToOneDrive(GraphServiceClient<Request> graphClient,
        String filePath, String itemPath) throws Exception {
        if (null == filePath || null == itemPath) {
            throw new Exception("Parameters are not optional");
        }
        // <LargeFileUploadSnippet>
        // Get an input stream for the file
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);
        long streamSize = file.length();

        final DriveItemCreateUploadSessionParameterSet uploadParams = DriveItemCreateUploadSessionParameterSet
            .newBuilder().withItem(new DriveItemUploadableProperties()).build();

        // Create an upload session
        final UploadSession uploadSession = graphClient.me().drive().root()
            .itemWithPath(itemPath).createUploadSession(uploadParams).buildRequest()
            .post();

        if (null == uploadSession) {
            fileStream.close();
            throw new Exception("Could not create upload session");
        }

        // Create a callback used by the upload provider
        final IProgressCallback callback = new IProgressCallback() {
            @Override
            // Called after each slice of the file is uploaded
            public void progress(final long current, final long max) {
                System.out.println(
                    String.format("Uploaded %d bytes of %d total bytes", current, max));
            }
        };

        LargeFileUploadTask<DriveItem> largeFileUploadTask = new LargeFileUploadTask<DriveItem>(
            uploadSession, graphClient, fileStream, streamSize, DriveItem.class);

        // Do the upload
        largeFileUploadTask.upload(0, null, callback);
        // </LargeFileUploadSnippet>
    }

    private static void uploadAttachmentToMessage(GraphServiceClient<Request> graphClient,
        String filePath) throws Exception {
        if (null == filePath) {
            throw new Exception("Parameters are not optional.");
        }

        // <UploadAttachmentSnippet>
        // Create message
        final Message draftMessage = new Message();
        draftMessage.subject = "Large attachment";

        final Message savedDraft = graphClient.me().messages().buildRequest()
            .post(draftMessage);

        // Get an input stream for the file
        File file = new File(filePath);
        InputStream fileStream = new FileInputStream(file);

        final AttachmentItem largeAttachment = new AttachmentItem();
        largeAttachment.attachmentType = AttachmentType.FILE;
        largeAttachment.name = file.getName();
        largeAttachment.size = file.length();

        final AttachmentCreateUploadSessionParameterSet uploadParams = AttachmentCreateUploadSessionParameterSet
            .newBuilder().withAttachmentItem(largeAttachment).build();

        final String draftId = savedDraft.id;
        if (null == draftId) {
            fileStream.close();
            throw new Exception("");
        }
        final UploadSession uploadSession = graphClient.me().messages(draftId)
            .attachments().createUploadSession(uploadParams).buildRequest().post();

        if (null == uploadSession) {
            fileStream.close();
            throw new Exception("Could not create upload session");
        }

        // Create a callback used by the upload provider
        final IProgressCallback callback = new IProgressCallback() {
            @Override
            // Called after each slice of the file is uploaded
            public void progress(final long current, final long max) {
                System.out.println(
                    String.format("Uploaded %d bytes of %d total bytes", current, max));
            }
        };

        LargeFileUploadTask<FileAttachment> uploadTask = new LargeFileUploadTask<FileAttachment>(
            uploadSession, graphClient, fileStream, file.length(), FileAttachment.class);

        // Do the upload
        uploadTask.upload(0, null, callback);
        // </UploadAttachmentSnippet>
    }
}
