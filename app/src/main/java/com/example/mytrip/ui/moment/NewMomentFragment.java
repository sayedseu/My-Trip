package com.example.mytrip.ui.moment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mytrip.R;
import com.example.mytrip.app.ViewModelProviderFactory;
import com.example.mytrip.databinding.FragmentNewMomentBinding;
import com.example.mytrip.utils.NetworkInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;

public class NewMomentFragment extends DaggerFragment {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private static String imageLocation = null;
    private static Bitmap bitmap = null;
    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    RequestManager requestManager;
    private NewMomentViewModel mViewModel;
    private NavController navController;
    private FragmentNewMomentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewMomentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, providerFactory).get(NewMomentViewModel.class);
        configureAlertDialog();
        subscribeObserver();

        binding.button.setOnClickListener(click -> {
            if (binding.editText.getText().toString().isEmpty()) {
                binding.editText.setError("Required");
            } else if (!NetworkInfo.hasNetwork(requireContext())) {
                showSnackBar("No internet connection.");
            } else {
                if (bitmap == null) {
                    showSnackBar("Please select an image");
                } else {
                    binding.button.setEnabled(false);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    mViewModel.uploadImage(data, binding.editText.getText().toString());
                }
            }
        });
    }

    private void subscribeObserver() {
        mViewModel.observeUploadingResult().removeObservers(getViewLifecycleOwner());
        mViewModel.observeUploadingResult().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        binding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        binding.button.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        showSnackBar("Something went wrong. Please try again later.");
                        mViewModel.clearObserver();
                        break;
                    case SUCCESS:
                        binding.button.setEnabled(true);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        if (resource.data) {
                            showSnackBar("Uploading Successful.");
                            navController.popBackStack();
                            mViewModel.clearObserver();
                        }
                        break;
                }
            }
        });
    }

    private void configureAlertDialog() {
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(requireContext());
        alertDialogBuilder.setIcon(R.drawable.add_image);
        alertDialogBuilder.setTitle("Select Photo");
        alertDialogBuilder.setMessage("Choice any Option !! ");
        alertDialogBuilder.setNeutralButton("Camera", (dialogInterface, i) -> {
            takePhoto();
            dialogInterface.dismiss();
        });
        alertDialogBuilder.setPositiveButton("Gallary", ((dialogInterface, i) -> {
            selectPhoto();
            dialogInterface.dismiss();
        }));
        alertDialogBuilder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.parent.setVisibility(View.VISIBLE);
            requestManager
                    .asBitmap()
                    .load(imageLocation)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;
                            binding.imageView.setImageBitmap(resource);
                            binding.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            binding.imageView.setImageDrawable(placeholder);
                        }
                    });
        }

        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            assert (data != null);
            binding.parent.setVisibility(View.VISIBLE);
            requestManager
                    .asBitmap()
                    .load(data.getData())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;
                            binding.imageView.setImageBitmap(resource);
                            binding.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            binding.imageView.setImageDrawable(placeholder);
                        }
                    });
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.mytrip",
                        photoFile);
                imageLocation = photoURI.toString();
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void showSnackBar(String message) {
        Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
